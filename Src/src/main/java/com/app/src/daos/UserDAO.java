package com.app.src.daos;

import com.app.src.models.User;
import com.app.src.models.Account;
import com.app.src.utils.MySQLDatabaseConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class UserDAO extends AbstractDAO<User>{

    private static Connection connection;
    private static UserDAO instance;

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
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

    public User findByUserName(String userName) {
        User user = null;
        final String sql = "SELECT * FROM user WHERE User_name = ?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userName);

            resultSet = preparedStatement.executeQuery();

            // Dùng 'if' thay vì 'while' vì ta chỉ cần lấy 1 user đầu tiên tìm được
            if (resultSet.next()) {
                user = new User();
                user.setUserId(resultSet.getInt("User_id"));
                user.setUserName(resultSet.getString("User_name"));

                // Theo code cũ của bạn, DoB được lưu dưới dạng String
                user.setUserDoB(resultSet.getString("User_dateOfBirth"));

                // Cột User_gender trong ảnh lưu giá trị 0 hoặc 1, nên dùng getInt
                user.setUserGender(resultSet.getInt("User_gender")==1);

                user.setUserPhoneNumber(resultSet.getString("User_phoneNumber"));
                System.out.println("Đã lấy được user " + user.getUserId() + " " + user.getUserName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                // Có ResultSet nên truyền đầy đủ cả 3 tham số để đóng
                closeResource(preparedStatement, connection, resultSet);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public boolean create(User entity) {
        return false;
    }

    @Override
    public boolean update(int id, User entity) {
        String sql = "UPDATE user SET User_name = ?, User_dateOfBirth = ?, User_gender = ?, User_phoneNumber = ? WHERE User_id = ?";

        PreparedStatement ps = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setString(1, entity.getUserName());
            ps.setString(2, entity.getUserDoB());
            ps.setBoolean(3, entity.isUserGender());
            ps.setString(4, entity.getUserPhoneNumber());
            ps.setInt(5, id);

            int affectedRows = ps.executeUpdate();
            closeResource(ps, connection);
            return affectedRows > 0;
        } catch (SQLException ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ignored) {
                // Ignore rollback failure and return false.
            }
            System.err.println("Error updating user profile: " + ex.getMessage());
            return false;
        } finally {
            try {
                closeConnection(connection);
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean delete(int id) {
        return false;
    }

    public boolean registerUser(String username, String name, String phone, String dob, String gender, String password) {
        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psAccount = null;
        ResultSet rs = null;

        try {
            conn = this.getConnection();
            conn.setAutoCommit(false); // Tắt tự động lưu để bắt đầu Transaction

            // 1. Tạo User trước
            String sqlUser = "INSERT INTO user (User_name, User_dateOfBirth, User_gender, User_phoneNumber) VALUES (?, ?, ?, ?)";
            psUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);   // Yêu cầu trả về ID tự động sinh ra
            psUser.setString(1, name);
            psUser.setString(2, dob);
            psUser.setBoolean(3, "Male".equalsIgnoreCase(gender)); // Ép kiểu String sang boolean
            psUser.setString(4, phone);
            psUser.executeUpdate();

            // Lấy ID của User vừa tạo
            rs = psUser.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            // 2. Tạo Account với userId vừa lấy
            String sqlAccount = "INSERT INTO account (Acc_userName, Acc_password, User_id) VALUES (?, ?, ?)";
            psAccount = conn.prepareStatement(sqlAccount);
            psAccount.setString(1, username);
            psAccount.setString(2, password);
            psAccount.setInt(3, userId);
            psAccount.executeUpdate();

            // Nếu không có lỗi, xác nhận lưu cả 2 bảng
            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có lỗi (ví dụ trùng username)
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (psUser != null) psUser.close();
                if (psAccount != null) psAccount.close();
                if (rs != null) rs.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Trả lại trạng thái mặc định
                    this.closeConnection(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
