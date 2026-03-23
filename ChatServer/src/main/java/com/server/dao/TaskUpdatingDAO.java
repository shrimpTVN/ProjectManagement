package com.server.dao;

import com.server.model.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskUpdatingDAO extends AbstractDAO<Task> {
	private static final String DEFAULT_STATUS = "TODO";

	public boolean createStatusUpdate(int taskId, int statusId, int userId, String content) {
		if (taskId <= 0 || statusId <= 0 || userId <= 0) {
			return false;
		}

		final String insertUpdateSql = "INSERT INTO STATUS_UPDATING(StU_date, StU_content, Task_id, Sta_id, User_id) VALUES(?, ?, ?, ?, ?)";
		final String upsertLastUpdatedSql = "INSERT INTO TASK_LAST_UPDATED(Task_id, Last_updated_at) VALUES(?, ?) "
				+ "ON DUPLICATE KEY UPDATE Last_updated_at = VALUES(Last_updated_at)";

		LocalDateTime now = LocalDateTime.now();
		Timestamp nowTimestamp = Timestamp.valueOf(now);

		try (Connection connection = getConnection();
			 PreparedStatement insertUpdate = connection.prepareStatement(insertUpdateSql);
			 PreparedStatement upsertLastUpdated = connection.prepareStatement(upsertLastUpdatedSql)) {

			insertUpdate.setTimestamp(1, nowTimestamp);
			insertUpdate.setString(2, content);
			insertUpdate.setInt(3, taskId);
			insertUpdate.setInt(4, statusId);
			insertUpdate.setInt(5, userId);

			if (insertUpdate.executeUpdate() <= 0) {
				connection.rollback();
				return false;
			}

			upsertLastUpdated.setInt(1, taskId);
			upsertLastUpdated.setTimestamp(2, nowTimestamp);
			if (upsertLastUpdated.executeUpdate() <= 0) {
				connection.rollback();
				return false;
			}

			connection.commit();
			return true;
		} catch (SQLException e) {
			System.err.println("[TaskUpdatingDAO] createStatusUpdate failed: " + e.getMessage());
			return false;
		}
	}

	public Map<Integer, String> getLatestStatusByTaskIds(List<Integer> taskIds) {
		Map<Integer, String> taskStatusMap = new HashMap<>();
		if (taskIds == null || taskIds.isEmpty()) {
			return taskStatusMap;
		}

		List<Integer> validTaskIds = new ArrayList<>();
		for (Integer taskId : taskIds) {
			if (taskId != null && taskId > 0) {
				validTaskIds.add(taskId);
			}
		}
		if (validTaskIds.isEmpty()) {
			return taskStatusMap;
		}

		String placeholders = String.join(",", java.util.Collections.nCopies(validTaskIds.size(), "?"));
		final String sql = "SELECT su.Task_id, ts.Sta_name "
				+ "FROM STATUS_UPDATING su "
				+ "JOIN TASK_STATUS ts ON ts.Sta_id = su.Sta_id "
				+ "JOIN (SELECT Task_id, MAX(StU_id) AS latest_stu_id FROM STATUS_UPDATING WHERE Task_id IN (" + placeholders + ") GROUP BY Task_id) latest "
				+ "ON latest.latest_stu_id = su.StU_id";

		try (Connection connection = getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {

			for (int i = 0; i < validTaskIds.size(); i++) {
				statement.setInt(i + 1, validTaskIds.get(i));
			}

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					taskStatusMap.put(resultSet.getInt("Task_id"), resultSet.getString("Sta_name"));
				}
			}

			connection.commit();
		} catch (SQLException e) {
			System.err.println("[TaskUpdatingDAO] getLatestStatusByTaskIds failed: " + e.getMessage());
		}

		return taskStatusMap;
	}

	public LocalDateTime getLastUpdatedAt(int taskId) {
		if (taskId <= 0) {
			return null;
		}

		final String sql = "SELECT Last_updated_at FROM TASK_LAST_UPDATED WHERE Task_id = ?";
		try (Connection connection = getConnection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {

			statement.setInt(1, taskId);
			try (ResultSet resultSet = statement.executeQuery()) {
				connection.commit();
				if (resultSet.next()) {
					Timestamp timestamp = resultSet.getTimestamp("Last_updated_at");
					return timestamp != null ? timestamp.toLocalDateTime() : null;
				}
			}
		} catch (SQLException e) {
			System.err.println("[TaskUpdatingDAO] getLastUpdatedAt failed: " + e.getMessage());
		}
		return null;
	}

	public void applyLatestStatus(List<Task> tasks) {
		if (tasks == null || tasks.isEmpty()) {
			return;
		}

		List<Integer> taskIds = new ArrayList<>(tasks.size());
		for (Task task : tasks) {
			if (task != null && task.getTaskId() > 0) {
				taskIds.add(task.getTaskId());
			}

									@Override
									public Task findById(int id) {
										return null;
									}

									@Override
									public List<Task> findAll() {
										return List.of();
									}

									@Override
									public boolean create(Task entity) {
										return false;
									}

									@Override
									public boolean update(int id, Task entity) {
										return false;
									}

									@Override
									public boolean delete(int id) {
										return false;
									}
		}

		Map<Integer, String> latestStatuses = getLatestStatusByTaskIds(taskIds);
		for (Task task : tasks) {
			if (task == null) {
				continue;
			}
			String status = latestStatuses.get(task.getTaskId());
			task.setTaskStatus(status == null || status.isBlank() ? DEFAULT_STATUS : status);
		}
	}
}
