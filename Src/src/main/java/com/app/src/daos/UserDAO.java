package com.app.src.daos;

import com.app.src.models.User;
import com.app.src.models.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class UserDAO extends AbstractDAO<User>{

    private static Connection connection;
    private static UserDAO instance;

    private UserDAO() {}

    public static UserDAO getInstance(){
        if(instance == null){
            instance = new UserDAO();

        }
        return instance;
    }

    @Override
    public User findById(int id) {
        String sql = "select * from user where User_id = ?";
        User user = new User();
        try{
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){

                user.setUserId(id);
                user.setUserName(rs.getString("User_name"));
                user.setUserDoB(rs.getString("User_dateOfBirth"));
                user.setUserGender(rs.getBoolean("User_gender"));
                user.setUserPhoneNumber(rs.getString("User_phoneNumber"));

                // Fetch Account information
                Account account = fetchAccountByUserId(id);
                if(account != null) {
                    user.setAccount(account);
                }
            }

            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                closeConnection(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return user;
    }

    // Helper method to fetch Account by User ID
    private Account fetchAccountByUserId(int userId) {
        String sql = "select * from account where user_id = ?";
        Account account = null;
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                account = new Account();
                account.setAccountId(rs.getInt("Acc_id"));
                account.setUserName(rs.getString("Acc_userName"));
                account.setPassword(rs.getString("Acc_password"));
            }
            this.closeResource(ps, conn, rs);
        } catch (SQLException ex) {
            // Log error but don't throw - Account may not exist
            System.err.println("Error fetching account for user " + userId + ": " + ex.getMessage());
        } finally {
            try {
                if(conn != null) {
                    closeConnection(conn);
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        return account;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try {
            connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("User_id"));
                user.setUserName(rs.getString("User_name"));

                // Bạn có thể lấy thêm các trường khác nếu UI cần hiển thị
                user.setUserDoB(rs.getString("User_dateOfBirth"));
                user.setUserGender(rs.getBoolean("User_gender"));
                user.setUserPhoneNumber(rs.getString("User_phoneNumber"));

                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    @Override
    public boolean create(User entity) {
        return false;
    }

    @Override
    public boolean update(int id, User entity) {
        return false;
    }

    @Override
    public boolean delete(int id) {
        return false;
    }
}
