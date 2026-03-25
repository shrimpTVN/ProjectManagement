package com.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * DAO hỗ trợ đọc trạng thái cập nhật mới nhất từ bảng STATUS_UPDATING.
 */
public class TaskUpdatingDAO extends AbstractDAO<Object> {

    @Override
    public Object findById(int id) {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public List<Object> findAll() {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public boolean create(Object entity) {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public boolean update(int id, Object entity) {
        throw new UnsupportedOperationException("Not used");
    }

    @Override
    public boolean delete(int id) {
        throw new UnsupportedOperationException("Not used");
    }

    public Map<Integer, String> getLatestStatusForTasks(List<Integer> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return Collections.emptyMap();
        }

        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        for (int i = 0; i < taskIds.size(); i++) {
            joiner.add("?");
        }

        final String sql = "SELECT su.Task_id, ts.Sta_name "
                + "FROM STATUS_UPDATING su "
                + "JOIN TASK_STATUS ts ON su.Sta_id = ts.Sta_id "
                + "WHERE su.Task_id IN " + joiner + " "
                + "AND su.StU_date = (SELECT MAX(su2.StU_date) FROM STATUS_UPDATING su2 WHERE su2.Task_id = su.Task_id)";

        Map<Integer, String> statusByTaskId = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int index = 1;
            for (Integer taskId : taskIds) {
                statement.setInt(index++, taskId);
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    statusByTaskId.put(rs.getInt("Task_id"), rs.getString("Sta_name"));
                }
            }
            connection.commit();
        } catch (SQLException e) {
            System.err.println("[TaskUpdatingDAO] getLatestStatusForTasks failed: " + e.getMessage());
        }

        return statusByTaskId;
    }
}

