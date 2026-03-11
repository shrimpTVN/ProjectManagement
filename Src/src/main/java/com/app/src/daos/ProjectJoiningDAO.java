package com.app.src.daos;

import com.app.src.models.ProjectJoining;
import com.app.src.models.ProjectRole;
import com.app.src.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        String adminName = "";
        final String sql = "select  user.user_name from project_joining PJ join user on PJ.user_id = user.user_id " +
                "where PJ.Role_id=2 and PJ.pro_id = ?";
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, projectId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                adminName = resultSet.getString("user_name");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return adminName;
    }

    // Method tổng quát để gán vai trò (Admin hoặc Manager)
    public boolean assignRole(int projectId, int userId, int roleId) {
        final String sql = "INSERT INTO PROJECT_JOINING (Pro_id, User_id, Role_id, PJo_dateJoin) VALUES (?, ?, ?, ?)";
        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            ps.setInt(3, roleId);
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {         // Nếu có ít nhất một hàng bị ảnh hưởng, commit transaction
                connection.commit();
            }
            ps.close();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (connection != null) {
                    closeConnection(connection);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean assignRoleManager(int projectId, int userId, int roleId) {
        return assignRole(projectId, userId, roleId);
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

    public ArrayList<ProjectJoining> findAllJoiningsByProjectId(int projectId) {
        ArrayList<ProjectJoining> projectJoinings = new ArrayList<>();
        final String sql = "SELECT pj.*, u.User_name, pr.Role_name " +
                "FROM project_joining pj " +
                "JOIN user u ON pj.User_id = u.User_id " +
                "JOIN project_role pr ON pj.Role_id = pr.Role_id " +
                "WHERE pj.Pro_id = ?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, projectId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ProjectJoining projectJoining = new ProjectJoining();

                User user = new User();
                user.setUserId(resultSet.getInt("User_id"));
                user.setUserName(resultSet.getString("User_name"));
                projectJoining.setUser(user);

                ProjectRole projectRole = new ProjectRole();
                projectRole.setRoleId(resultSet.getInt("Role_id"));
                projectRole.setRoleName(resultSet.getString("Role_name"));
                projectJoining.setRole(projectRole);

                projectJoining.setJoinDate(resultSet.getTimestamp("PJo_dateJoin"));
                projectJoinings.add(projectJoining);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                closeResource(preparedStatement, connection, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return projectJoinings;
    }

    public boolean updateManager(int projectId, int newManagerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Gom thành 1 khối để tránh lỗi giữa chừng

            // 1. Kiểm tra vai trò hiện tại của người được chọn
            String checkSql = "SELECT Role_id FROM project_joining WHERE Pro_id = ? AND User_id = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, projectId);
            ps.setInt(2, newManagerId);
            rs = ps.executeQuery();

            boolean userExists = rs.next();
            int newRole = userExists ? rs.getInt("Role_id") : -1;

            rs.close();
            ps.close();

            // 2. Đổi Manager cũ thành Member (Role_id = 3)
            String demoteOldManager = "UPDATE project_joining SET Role_id = 3 WHERE Pro_id = ? AND Role_id = 1";
            ps = conn.prepareStatement(demoteOldManager);
            ps.setInt(1, projectId);
            ps.executeUpdate();
            ps.close();

            // 3. Xử lý người quản lý mới
            if (userExists) {
                if (newRole == 3) {
                    // Thăng chức Member lên Manager
                    String promoteToManager = "UPDATE project_joining SET Role_id = 1 WHERE Pro_id = ? AND User_id = ?";
                    ps = conn.prepareStatement(promoteToManager);
                    ps.setInt(1, projectId);
                    ps.setInt(2, newManagerId);
                    ps.executeUpdate();
                }
                // Nếu newRole == 2 (Admin) thì giữ nguyên, hệ thống tự hiểu Admin kiêm nhiệm
            } else {
                // Người mới hoàn toàn chưa có trong dự án -> Thêm vào với Role_id = 1
                String insertManager = "INSERT INTO project_joining (Pro_id, User_id, Role_id) VALUES (?, ?, 1)";
                ps = conn.prepareStatement(insertManager);
                ps.setInt(1, projectId);
                ps.setInt(2, newManagerId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                closeResource(ps, conn, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}