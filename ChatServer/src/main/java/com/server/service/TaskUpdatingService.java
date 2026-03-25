package com.server.service;

import com.server.dao.TaskUpdatingDAO;
import com.server.model.Task;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskUpdatingService {
    public static final String DONE_STATUS = "DONE";

    private final TaskUpdatingDAO taskUpdatingDAO = new TaskUpdatingDAO();

    public void applyLatestStatus(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        List<Integer> taskIds = tasks.stream()
                .map(Task::getTaskId)
                .filter(id -> id > 0)
                .collect(Collectors.toList());
        if (taskIds.isEmpty()) {
            return;
        }

        Map<Integer, String> latestStatuses = taskUpdatingDAO.getLatestStatusForTasks(taskIds);
        for (Task task : tasks) {
            String status = latestStatuses.get(task.getTaskId());
            if (status != null) {
                task.setTaskStatus(status);
            }
        }
    }

    public String normalizeStatus(String status) {
        if (status == null) {
            return "";
        }
        return status.trim().toUpperCase(Locale.ROOT);
    }
}

