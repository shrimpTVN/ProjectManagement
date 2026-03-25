package com.server.thread;

import com.server.core.ChatServer;
import com.server.model.Notification;
import com.server.model.Task;
import com.server.service.NotificationService;
import com.server.service.TaskService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DailyDeadlineScheduler {
    private static final int REMINDER_WINDOW_DAYS = 3;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean jobRunning = new AtomicBoolean(false);

    private final TaskService taskService;
    private final NotificationService notificationService;

    private volatile boolean started;

    public DailyDeadlineScheduler(TaskService taskService, NotificationService notificationService) {
        this.taskService = taskService;
        this.notificationService = notificationService;
    }

    public void start() {
        if (started) {
            return;
        }

        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();

        long initialDelay = Duration.between(now, nextMidnight).toMillis();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(this::runDailyCheck, initialDelay, period, TimeUnit.MILLISECONDS);
        started = true;

        System.out.println("Midnight scheduler started. First run in " + (initialDelay / 1000 / 60) + " minutes.");
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void runDailyCheck() {
        if (!jobRunning.compareAndSet(false, true)) {
            return;
        }

        try {
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            processExpiringTasks(today);
        } catch (Exception e) {
            System.err.println("Error during deadline scheduler run: " + e.getMessage());
        } finally {
            jobRunning.set(false);
        }
    }

    private void processExpiringTasks(LocalDate checkDate) {
        List<Task> expiringTasks = taskService.getTasksForDeadlineReminder(REMINDER_WINDOW_DAYS, checkDate);
        if (expiringTasks.isEmpty()) {
            return;
        }

        List<Integer> notifiedTaskIds = new ArrayList<>();
        for (Task task : expiringTasks) {
            Notification notification = new Notification();
            notification.setUserId(task.getUserId());
            notification.setNotiTitle("Task deadline reminder");
            notification.setNotiDescription("Reminder: Task '" + task.getTaskName() + "' is approaching its deadline!");
            notification.setNotiIsRead(false);
            notification.setNotiTime(LocalDateTime.now().toString());

            boolean persisted = notificationService.createNotification(notification);
            if (!persisted) {
                System.err.println("Failed to persist reminder for task " + task.getTaskId());
                continue;
            }

            ChatServer.sendNotificationToUser(task.getUserId(), notification);
            notifiedTaskIds.add(task.getTaskId());
        }

        if (!notifiedTaskIds.isEmpty()) {
            taskService.markTasksAsNotified(notifiedTaskIds);
        }
    }
}

