package com.app.src.daos;

import com.app.src.models.Project;
import com.app.src.models.ProjectJoining;
import com.app.src.models.ProjectRole;
import com.app.src.models.User;

import javax.management.relation.Role;
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

    public int getRoleId(int userID, int projectID) {
        int roleId = 0;
        final String sql ="select role_id\n" +
                "from project_joining\n" +
                "where user_id=? and pro_id=?;";
        try{
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, projectID);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roleId = resultSet.getInt("role_id");
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
        return roleId;
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

    public boolean createNewJoining(int project, int user, int role) {
        final String sql = "INSERT INTO project_joining (PJo_dateJoin, User_id, Pro_id, Role_id) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = null;
        boolean isSuccess = false; // Biến lưu kết quả

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);

            java.sql.Timestamp currentTime = new java.sql.Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(1, currentTime);
            preparedStatement.setInt(2, user);
            preparedStatement.setInt(3, project);
            preparedStatement.setInt(4, role);

            // executeUpdate() trả về số dòng bị ảnh hưởng. Nếu > 0 tức là insert thành công.
            int rowAffected = preparedStatement.executeUpdate();
            isSuccess = rowAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            // Thay vì throw RuntimeException làm crash app, mình in ra lỗi và để hàm trả về false
        } finally {
            try {
                closeResource(preparedStatement, connection, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println(isSuccess + "...");
        return isSuccess; // Trả về true hoặc false
    }

    public ArrayList<ProjectJoining> findAllJoiningsByProjectId(int projectId) {
        ArrayList<ProjectJoining> projectJoinings = new ArrayList<>();
        final String sql = "SELECT pj.*, u.User_name, u.User_dateOfBirth, u.User_phoneNumber, pr.Role_name " +
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
                user.setUserDoB(resultSet.getString("User_dateOfBirth"));
                user.setUserPhoneNumber(resultSet.getString("User_phoneNumber"));
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

    public boolean updateRole(int projectId, int userId, int roleId) {
        String sql = "UPDATE project_joining SET Role_id = ? WHERE Pro_id = ? AND User_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, roleId);
            ps.setInt(2, projectId);
            ps.setInt(3, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeResource(ps, conn, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean removeMember(int projectId, int userId) {
        String sql = "DELETE FROM project_joining WHERE Pro_id = ? AND User_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, projectId);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                closeResource(ps, conn, null);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}