package com.server.service;

import com.server.dao.TaskDAO;
import com.server.model.Task;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
	private final TaskDAO taskDAO = new TaskDAO();
	private final TaskUpdatingService taskUpdatingService = new TaskUpdatingService();

	public Task getTaskById(int taskId) {
		if (taskId <= 0) {
			return null;
		}
		Task task = taskDAO.findById(taskId);
		if (task != null) {
			taskUpdatingService.applyLatestStatus(List.of(task));
		}
		return task;
	}

	public List<Task> getAllTasks() {
		List<Task> tasks = taskDAO.findAll();
		taskUpdatingService.applyLatestStatus(tasks);
		return tasks;
	}

	public boolean createTask(Task task) {
		return task != null && taskDAO.create(task);
	}

	public boolean updateTask(int taskId, Task task) {
		return taskId > 0 && task != null && taskDAO.update(taskId, task);
	}

	public boolean deleteTask(int taskId) {
		return taskId > 0 && taskDAO.delete(taskId);
	}

	public List<Task> getTasksNearingDeadline(int days) {
		if (days <= 0) {
			return List.of();
		}
		return getTasksNearingDeadline(days, LocalDate.now());
	}

	public List<Task> getTasksNearingDeadline(int days, LocalDate referenceDate) {
		if (days <= 0) {
			return List.of();
		}
		List<Task> tasks = taskDAO.findTasksNearingDeadline(days, referenceDate);
		taskUpdatingService.applyLatestStatus(tasks);
		return tasks.stream()
				.filter(task -> !TaskUpdatingService.DONE_STATUS.equals(taskUpdatingService.normalizeStatus(task.getTaskStatus())))
				.collect(Collectors.toList());
	}

	public List<Task> getTasksForDeadlineReminder(int days, LocalDate referenceDate) {
		if (days <= 0) {
			return List.of();
		}
		List<Task> tasks = taskDAO.findTasksForDeadlineReminder(days, referenceDate, TaskUpdatingService.DONE_STATUS);
		taskUpdatingService.applyLatestStatus(tasks);
		return tasks;
	}

	public boolean markTasksAsNotified(List<Integer> taskIds) {
		return taskDAO.markTasksAsNotified(taskIds);
	}
}
