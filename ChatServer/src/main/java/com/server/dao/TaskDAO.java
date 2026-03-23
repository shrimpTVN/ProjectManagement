package com.server.dao;

import com.server.model.Task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskDAO extends AbstractDAO<Task> {
    @Override
    public Task findById(int id) {
        final String sql = "SELECT t.Task_id, t.Task_name, t.Task_description, t.Task_startDate, t.Task_endDate, t.Pro_id, t.User_id "
                + "FROM TASK t WHERE t.Task_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                connection.commit();
                if (resultSet.next()) {
                    return mapTask(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("[TaskDAO] findById failed: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Task> findAll() {
        final String sql = "SELECT t.Task_id, t.Task_name, t.Task_description, t.Task_startDate, t.Task_endDate, t.Pro_id, t.User_id "
                + "FROM TASK t";
        List<Task> tasks = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tasks.add(mapTask(resultSet));
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println("[TaskDAO] findAll failed: " + e.getMessage());
        }
        return tasks;
    }

    @Override
    public boolean create(Task task) {
        if (task == null) {
            return false;
        }

        final String sql = "INSERT INTO TASK(Task_name, Task_description, Task_startDate, Task_endDate, Pro_id, User_id) "
                + "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getTaskDescription());
            statement.setString(3, task.getTaskStartTime());
            statement.setString(4, task.getTaskEndTime());
            setNullableInt(statement, 5, task.getProjectId());
            setNullableInt(statement, 6, task.getUserId());

            boolean created = statement.executeUpdate() > 0;
            connection.commit();
            return created;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] create failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(int id, Task task) {
        if (task == null) {
            return false;
        }

        final String sql = "UPDATE TASK SET Task_name = ?, Task_description = ?, Task_startDate = ?, Task_endDate = ?, "
                + "Pro_id = ?, User_id = ? WHERE Task_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getTaskDescription());
            statement.setString(3, task.getTaskStartTime());
            statement.setString(4, task.getTaskEndTime());
            setNullableInt(statement, 5, task.getProjectId());
            setNullableInt(statement, 6, task.getUserId());
            statement.setInt(7, id);

            boolean updated = statement.executeUpdate() > 0;
            connection.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] update failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        final String sql = "DELETE FROM TASK WHERE Task_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            boolean deleted = statement.executeUpdate() > 0;
            connection.commit();
            return deleted;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] delete failed: " + e.getMessage());
            return false;
        }
    }

    public List<Task> findTasksNearingDeadline(int days) {
        return findTasksNearingDeadline(days, LocalDate.now());
    }

    public List<Task> findTasksNearingDeadline(int days, LocalDate referenceDate) {
        if (days <= 0) {
            return Collections.emptyList();
        }
        if (referenceDate == null) {
            referenceDate = LocalDate.now();
        }

        final String sql = "SELECT t.Task_id, t.Task_name, t.Task_description, t.Task_startDate, t.Task_endDate, "
                + "t.Pro_id, t.User_id "
                + "FROM TASK t "
                + "WHERE DATE(t.Task_endDate) BETWEEN ? AND DATE_ADD(?, INTERVAL ? DAY)";

        List<Task> tasks = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            Date fromDate = Date.valueOf(referenceDate);
            statement.setDate(1, fromDate);
            statement.setDate(2, fromDate);
            statement.setInt(3, days);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(mapTask(resultSet));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println("[TaskDAO] findTasksNearingDeadline failed: " + e.getMessage());
        }

        return tasks;
    }

    private Task mapTask(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setTaskId(resultSet.getInt("Task_id"));
        task.setTaskName(resultSet.getString("Task_name"));
        task.setTaskDescription(resultSet.getString("Task_description"));
        task.setTaskStartTime(resultSet.getString("Task_startDate"));
        task.setTaskEndTime(resultSet.getString("Task_endDate"));
        task.setTaskStatus(null);

        int projectId = resultSet.getInt("Pro_id");
        task.setProjectId(resultSet.wasNull() ? 0 : projectId);

        int userId = resultSet.getInt("User_id");
        task.setUserId(resultSet.wasNull() ? 0 : userId);
        return task;
    }

    private void setNullableInt(PreparedStatement statement, int index, int value) throws SQLException {
        if (value <= 0) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}
