package com.app.src.daos;

import com.app.src.models.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO extends AbstractDAO {
    private static Connection connection;
    private static CommentDAO instance;

    private CommentDAO() {
    }

    public static CommentDAO getInstance() {
        if (instance == null) {
            instance = new CommentDAO();
        }
        return instance;
    }

    public int getLatestOtherUserCommentId(int taskId, int currentUserId) {
        int latestId = 0;
        // Tìm Com_id của Task này nhưng User_id PHẢI KHÁC currentUserId
        final String sql = "SELECT Com_id FROM comment WHERE Task_id = ? AND User_id != ? ORDER BY Com_date DESC LIMIT 1";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, taskId);
            ps.setInt(2, currentUserId);
            rs = ps.executeQuery();
            if (rs.next()) {
                latestId = rs.getInt("Com_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeResource(ps, connection, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return latestId;
    }

    public List<Comment> getCommentsByTaskId(int taskId) {
        List<Comment> comments = new ArrayList<>();
        final String sql = "SELECT c.*, u.User_name FROM comment c " +
                "JOIN user u ON c.User_id = u.User_id " +
                "WHERE c.Task_id = ? ORDER BY c.Com_date ASC";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, taskId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Comment comment = new Comment(
                        resultSet.getInt("Com_id"),
                        resultSet.getInt("Task_id"),
                        resultSet.getString("Com_description"),
                        resultSet.getTimestamp("Com_date"),
                        resultSet.getInt("User_id"),
                        resultSet.getInt("Previous_Com_id")
                );
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeResource(preparedStatement, connection, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }

    public boolean insertComment(Comment comment) {
        final String sql = "INSERT INTO comment (Task_id, Com_description, Com_date, User_id, Previous_Com_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        boolean isSuccess = false;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, comment.getTaskId());
            preparedStatement.setString(2, comment.getComment());
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setInt(4, comment.getUserId());
            preparedStatement.setInt(5, comment.getPreviousCommentId());

            int rowAffected = preparedStatement.executeUpdate();
            isSuccess = rowAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                closeResource(preparedStatement, connection, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSuccess;
    }

    @Override
    public Object findById(int id) {
        return null;
    }

    @Override
    public List findAll() {
        return List.of();
    }

    @Override
    public boolean create(Object entity) {
        return false;
    }

    @Override
    public boolean update(int id, Object entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}