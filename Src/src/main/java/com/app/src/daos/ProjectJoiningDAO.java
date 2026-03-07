package com.app.src.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ProjectJoiningDAO extends AbstractDAO {
    private static Connection connection;
    private static ProjectJoiningDAO instance;

    private ProjectJoiningDAO() {
    }

   public static ProjectJoiningDAO getInstance() {
        if (instance == null) {
            instance = new ProjectJoiningDAO();
        }
        return instance;
    }

    public String getAdmin(int projectId) {
        String adminName ="";
        final String sql ="select  user.user_name from project_joining PJ join user on PJ.user_id = user.user_id " +
                "where PJ.Role_id=2 and PJ.pro_id = ?";
        try{
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                adminName = resultSet.getString("user_name");
            }

        } catch(Exception e)
        {
            e.printStackTrace();
        } finally {
            try {
                closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return  adminName;
    }

    public boolean assignRole(int projectId, int userId, int roleId) {
        final String sql = "INSERT INTO PROJECT_JOINING (Pro_id, User_id, Role_id, PJo_dateJoin) VALUES (?, ?, ?, ?)";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            ps.setInt(3, roleId);
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis())); // này là để lấy thời gian hiện tại

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    closeConnection(connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
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
