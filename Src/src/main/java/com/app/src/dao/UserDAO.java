package com.app.src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// import java class
import com.app.src.models.User;
import com.app.src.utils.Database;
public class UserDAO {

    // 1. Thêm người dùng mới
    public boolean insertUser(User user) {
        String sql = "INSERT INTO USER(User_id, User_name, User_dateOfBirth, User_sex, User_phoneNumber) VALUES(?,?,?,?,?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUserName());
            pstmt.setString(3, user.getDateOfBirth());
            pstmt.setString(4, user.getSex());
            pstmt.setInt(5, user.getPhoneNumber());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm User: " + e.getMessage());
            return false;
        }
    }

    // 2. Lấy danh sách tất cả người dùng
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USER";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("User_id"),
                        rs.getString("User_name"),
                        rs.getString("User_dateOfBirth"),
                        rs.getString("User_sex"),
                        rs.getInt("User_phoneNumber")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi lấy danh sách User: " + e.getMessage());
        }
        return users;
    }

    // 3. Cập nhật thông tin người dùng
    public boolean updateUser(User user) {
        String sql = "UPDATE USER SET User_name = ?, User_dateOfBirth = ?, User_sex = ?, User_phoneNumber = ? WHERE User_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getDateOfBirth());
            pstmt.setString(3, user.getSex());
            pstmt.setInt(4, user.getPhoneNumber());
            pstmt.setString(5, user.getUserId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi cập nhật User: " + e.getMessage());
            return false;
        }
    }

    // 4. Xóa người dùng
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM USER WHERE User_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi xóa User: " + e.getMessage());
            return false;
        }
    }
}