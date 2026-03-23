package com.server.thread;

import com.google.gson.Gson;
import com.server.core.ChatServer;
import com.server.model.Notification;
import com.server.model.Task;
import com.server.service.NotificationService;
import com.server.service.TaskService;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DailyDeadlineScheduler {
    private static final int REMINDER_WINDOW_DAYS = 3;
    private static final int SENT_KEY_RETENTION_DAYS = 45;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean jobRunning = new AtomicBoolean(false);
    private final Gson gson = new Gson();

    private final TaskService taskService;
    private final NotificationService notificationService;
    private final Path stateFilePath;

    private volatile boolean started;

    public DailyDeadlineScheduler(TaskService taskService, NotificationService notificationService) {
        this(taskService, notificationService, Paths.get("scheduler-state.json"));
    }

    public DailyDeadlineScheduler(TaskService taskService, NotificationService notificationService, Path stateFilePath) {
        this.taskService = taskService;
        this.notificationService = notificationService;
        this.stateFilePath = stateFilePath;
    }

    public void start() {
        if (started) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        long initialDelay = Duration.between(now, nextMidnight).toMillis();
        long period = TimeUnit.DAYS.toMillis(1);

        runDailyCheck();
        scheduler.scheduleAtFixedRate(this::runDailyCheck, initialDelay, period, TimeUnit.MILLISECONDS);
        started = true;

        System.out.println("Midnight scheduler started. Next run in " + (initialDelay / 3600000) + " hours.");
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void runDailyCheck() {
        if (!jobRunning.compareAndSet(false, true)) {
            return;
        }

        try {
            SchedulerState state = loadState();
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            LocalDate lastSuccessful = parseDate(state.lastSuccessfulCheckDate);
            if (lastSuccessful == null) {
                lastSuccessful = today.minusDays(1);
            }

            LocalDate dateToProcess = lastSuccessful.plusDays(1);
            while (!dateToProcess.isAfter(today)) {
                processExpiringTasksForDate(dateToProcess, state);
                state.lastSuccessfulCheckDate = dateToProcess.toString();
                pruneOldKeys(state, today);
                saveState(state);
                dateToProcess = dateToProcess.plusDays(1);
            }
        } catch (Exception e) {
            System.err.println("Error during deadline scheduler run: " + e.getMessage());
        } finally {
            jobRunning.set(false);
        }
    }

    private void processExpiringTasksForDate(LocalDate checkDate, SchedulerState state) {
        List<Task> expiringTasks = taskService.getTasksNearingDeadline(REMINDER_WINDOW_DAYS, checkDate);

        for (Task task : expiringTasks) {
            String reminderKey = buildReminderKey(checkDate, task.getTaskId());
            if (state.sentReminderKeys.contains(reminderKey)) {
                continue;
            }

            Notification notification = new Notification();
            notification.setUserId(task.getUserId());
            notification.setNotiTitle("Task deadline reminder");
            notification.setNotiDescription("Reminder: Task '" + task.getTaskName() + "' is approaching its deadline!");
            notification.setNotiIsRead(false);
            notification.setNotiTime(LocalDateTime.now().toString());

            boolean persisted = notificationService.createNotification(notification);
            if (!persisted) {
                throw new IllegalStateException("Failed to persist reminder for task " + task.getTaskId());
            }

            ChatServer.sendNotificationToUser(task.getUserId(), notification);

            state.sentReminderKeys.add(reminderKey);
            saveState(state);
        }
    }

    private SchedulerState loadState() {
        if (!Files.exists(stateFilePath)) {
            return new SchedulerState();
        }

        try (Reader reader = Files.newBufferedReader(stateFilePath)) {
            SchedulerState state = gson.fromJson(reader, SchedulerState.class);
            if (state == null) {
                return new SchedulerState();
            }
            if (state.sentReminderKeys == null) {
                state.sentReminderKeys = new HashSet<>();
            }
            return state;
        } catch (IOException e) {
            System.err.println("Failed to read scheduler state, fallback to default: " + e.getMessage());
            return new SchedulerState();
        }
    }

    private void saveState(SchedulerState state) {
        try {
            Path parent = stateFilePath.getParent();
            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(stateFilePath)) {
                gson.toJson(state, writer);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to persist scheduler state", e);
        }
    }

    private void pruneOldKeys(SchedulerState state, LocalDate today) {
        LocalDate minDate = today.minusDays(SENT_KEY_RETENTION_DAYS);
        state.sentReminderKeys.removeIf(key -> {
            LocalDate keyDate = parseDateFromKey(key);
            return keyDate != null && keyDate.isBefore(minDate);
        });
    }

    private String buildReminderKey(LocalDate checkDate, int taskId) {
        return checkDate + "#" + taskId;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDateFromKey(String key) {
        if (key == null) {
            return null;
        }
        int separator = key.indexOf('#');
        if (separator <= 0) {
            return null;
        }
        return parseDate(key.substring(0, separator));
    }

    private static class SchedulerState {
        private String lastSuccessfulCheckDate;
        private Set<String> sentReminderKeys = new HashSet<>();
    }
}

