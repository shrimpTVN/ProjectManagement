package com.app.src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// my import
import com.app.src.models.Account;
import com.app.src.utils.Database;
public class AccountDAO {

    // 1. Thêm tài khoản mới (Đăng ký)
    public boolean insertAccount(Account account) {
        String sql = "INSERT INTO ACCOUNT(Acc_userName, Acc_password, User_id) VALUES(?,?,?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccUserName());
            // Lưu ý: Trong thực tế, bạn nên mã hóa mật khẩu (vd: BCrypt) trước khi lưu
            pstmt.setString(2, account.getAccPassword());
            pstmt.setString(3, account.getUserId());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Lỗi thêm Account: " + e.getMessage());
            return false;
        }
    }

    // 2. Kiểm tra đăng nhập
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM ACCOUNT WHERE Acc_userName = ? AND Acc_password = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Nếu ResultSet có dữ liệu trả về nghĩa là tài khoản và mật khẩu khớp
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra đăng nhập: " + e.getMessage());
            return false;
        }
    }

    // 3. Đổi mật khẩu
    public boolean updatePassword(String username, String newPassword) {
        String sql = "UPDATE ACCOUNT SET Acc_password = ? WHERE Acc_userName = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, username);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật

        } catch (SQLException e) {
            System.out.println("Lỗi đổi mật khẩu: " + e.getMessage());
            return false;
        }
    }

    // 4. Xóa tài khoản
    public boolean deleteAccount(String username) {
        String sql = "DELETE FROM ACCOUNT WHERE Acc_userName = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi xóa Account: " + e.getMessage());
            return false;
        }
    }
}