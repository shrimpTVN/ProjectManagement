package com.server.service;

import com.server.dao.TaskUpdatingDAO;
import com.server.model.Task;

import java.time.LocalDateTime;
import java.util.List;

public class TaskUpdatingService {
	private final TaskUpdatingDAO taskUpdatingDAO = new TaskUpdatingDAO();

	public boolean updateTaskStatus(int taskId, int statusId, int userId, String content) {
		return taskUpdatingDAO.createStatusUpdate(taskId, statusId, userId, content);
	}

	public void applyLatestStatus(List<Task> tasks) {
		taskUpdatingDAO.applyLatestStatus(tasks);
	}

	public LocalDateTime getLastUpdatedAt(int taskId) {
		return taskUpdatingDAO.getLastUpdatedAt(taskId);
	}
}
