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
import java.util.StringJoiner;

public class TaskDAO extends AbstractDAO<Task> {
    @Override
    public Task findById(int id) {
        final String sql = "SELECT t.Task_id, t.Task_name, t.Task_description, t.Task_startDate, t.Task_endDate, t.Pro_id, t.User_id, t.isRejected, t.isNotified "
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
        final String sql = "SELECT t.Task_id, t.Task_name, t.Task_description, t.Task_startDate, t.Task_endDate, t.Pro_id, t.User_id, t.isRejected, t.isNotified "
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

        final String sql = "INSERT INTO TASK(Task_name, Task_description, Task_startDate, Task_endDate, Pro_id, User_id, isRejected, isNotified) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getTaskDescription());
            statement.setString(3, task.getTaskStartTime());
            statement.setString(4, task.getTaskEndTime());
            setNullableInt(statement, 5, task.getProjectId());
            setNullableInt(statement, 6, task.getUserId());
            statement.setBoolean(7, task.isRejected());
            statement.setBoolean(8, task.isNotified());

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
                + "Pro_id = ?, User_id = ?, isRejected = ?, isNotified = ? WHERE Task_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getTaskDescription());
            statement.setString(3, task.getTaskStartTime());
            statement.setString(4, task.getTaskEndTime());
            setNullableInt(statement, 5, task.getProjectId());
            setNullableInt(statement, 6, task.getUserId());
            statement.setBoolean(7, task.isRejected());
            statement.setBoolean(8, task.isNotified());
            statement.setInt(9, id);

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
                + "t.Pro_id, t.User_id, t.isRejected, t.isNotified, ls.Sta_name "
                + "FROM TASK t "
                + "LEFT JOIN (";
        final String latestStatusCte = "SELECT su.Task_id, ts.Sta_name, su.StU_date "
                + "FROM STATUS_UPDATING su "
                + "JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id) ls_raw";
        final String sqlTail = " ls ON ls.Task_id = t.Task_id "
                + "WHERE t.isRejected = false AND DATE(t.Task_endDate) BETWEEN ? AND DATE_ADD(?, INTERVAL ? DAY)";

        StringBuilder builder = new StringBuilder();
        builder.append(sql)
                .append(latestStatusCte)
                .append(" WHERE ls_raw.StU_date = (SELECT MAX(su2.StU_date) FROM STATUS_UPDATING su2 WHERE su2.Task_id = ls_raw.Task_id))")
                .append(sqlTail);

        List<Task> tasks = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(builder.toString())) {
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

    public List<Task> findTasksForDeadlineReminder(int days, LocalDate referenceDate, String doneStatusUpper) {
        if (days <= 0) {
            return Collections.emptyList();
        }
        if (referenceDate == null) {
            referenceDate = LocalDate.now();
        }

        final String sql = "SELECT t.Task_id, t.Task_name, t.Task_description, t.Task_startDate, t.Task_endDate, "
                + "t.Pro_id, t.User_id, t.isRejected, t.isNotified, ls.Sta_name "
                + "FROM TASK t "
                + "LEFT JOIN (";
        final String latestStatusCte = "SELECT su.Task_id, ts.Sta_name, su.StU_date "
                + "FROM STATUS_UPDATING su "
                + "JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id) ls_raw";
        final String sqlTail = " ls ON ls.Task_id = t.Task_id "
                + "WHERE t.isRejected = false AND t.isNotified = false "
                + "AND DATE(t.Task_endDate) BETWEEN ? AND DATE_ADD(?, INTERVAL ? DAY) "
                + "AND (ls.Sta_name IS NULL OR UPPER(ls.Sta_name) <> ? )";

        StringBuilder builder = new StringBuilder();
        builder.append(sql)
                .append(latestStatusCte)
                .append(" WHERE ls_raw.StU_date = (SELECT MAX(su2.StU_date) FROM STATUS_UPDATING su2 WHERE su2.Task_id = ls_raw.Task_id))")
                .append(sqlTail);

        List<Task> tasks = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(builder.toString())) {
            Date fromDate = Date.valueOf(referenceDate);
            statement.setDate(1, fromDate);
            statement.setDate(2, fromDate);
            statement.setInt(3, days);
            statement.setString(4, doneStatusUpper);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(mapTask(resultSet));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println("[TaskDAO] findTasksForDeadlineReminder failed: " + e.getMessage());
        }

        return tasks;
    }

    public boolean markTasksAsNotified(List<Integer> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return false;
        }
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < taskIds.size(); i++) {
            joiner.add("?");
        }
        final String sql = "UPDATE TASK SET isNotified = true WHERE Task_id IN " + joiner;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Integer taskId : taskIds) {
                statement.setInt(index++, taskId);
            }
            boolean updated = statement.executeUpdate() > 0;
            connection.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("[TaskDAO] markTasksAsNotified failed: " + e.getMessage());
            return false;
        }
    }

    private Task mapTask(ResultSet resultSet) throws SQLException {
        Task task = new Task();
        task.setTaskId(resultSet.getInt("Task_id"));
        task.setTaskName(resultSet.getString("Task_name"));
        task.setTaskDescription(resultSet.getString("Task_description"));
        task.setTaskStartTime(resultSet.getString("Task_startDate"));
        task.setTaskEndTime(resultSet.getString("Task_endDate"));
        task.setTaskStatus(resultSet.getString("Sta_name"));

        int projectId = resultSet.getInt("Pro_id");
        task.setProjectId(resultSet.wasNull() ? 0 : projectId);

        int userId = resultSet.getInt("User_id");
        task.setUserId(resultSet.wasNull() ? 0 : userId);
        task.setRejected(resultSet.getBoolean("isRejected"));
        task.setNotified(resultSet.getBoolean("isNotified"));
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
