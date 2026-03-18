package com.app.src.daos;

import com.app.src.models.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO implements BaseDAO<Notification> {

    // Giả định bạn có một class tiện ích để lấy kết nối Database
    // Hãy thay thế bằng cách lấy Connection thực tế của project bạn
    private Connection getConnection() throws SQLException {
        // return DBUtils.getConnection();
        return null;
    }

    @Override
    public Notification findById(int id) {
        Notification notification = null;
        String sql = "SELECT * FROM notifications WHERE noti_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    notification = new Notification();
                    notification.setNotiId(rs.getInt("noti_id"));
                    notification.setNotiDescription(rs.getString("noti_description"));
                    notification.setNotiIsRead(rs.getBoolean("noti_is_read"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notification;
    }
    public List<Notification> findByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();

        // Cập nhật câu SQL để lọc theo user_id
        // (Lưu ý: Giả định cột khóa ngoại trong DB của bạn tên là 'user_id', hãy sửa lại nếu tên cột khác nhé)
        String sql = "SELECT * FROM notifications WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Truyền tham số userId vào dấu '?'
            stmt.setInt(1, userId);

            // 2. Thực thi và duyệt qua kết quả trả về
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Dùng constructor giống như cách bạn đã viết trong hàm findAll()
                    Notification noti = new Notification(
                            rs.getInt("noti_id"),
                            rs.getString("noti_description"),
                            rs.getBoolean("noti_is_read")
                    );
                    notifications.add(noti);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notifications;
    }
    @Override
    public List<Notification> findAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Notification noti = new Notification(
                        rs.getInt("noti_id"),
                        rs.getString("noti_description"),
                        rs.getBoolean("noti_is_read")
                );
                notifications.add(noti);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    @Override
    public boolean create(Notification entity) {
        // Thường thì ID sẽ tự tăng trong DB (Auto Increment) nên không cần INSERT ID
        String sql = "INSERT INTO notifications (noti_description, noti_is_read) VALUES (?, ?)";
        boolean isSuccess = false;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getNotiDescription());
            stmt.setBoolean(2, entity.isNotiIsRead());

            int rowsAffected = stmt.executeUpdate();
            isSuccess = rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean update(int id, Notification entity) {
        String sql = "UPDATE notifications SET noti_description = ?, noti_is_read = ? WHERE noti_id = ?";
        boolean isSuccess = false;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getNotiDescription());
            stmt.setBoolean(2, entity.isNotiIsRead());
            stmt.setInt(3, id);

            int rowsAffected = stmt.executeUpdate();
            isSuccess = rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM notifications WHERE noti_id = ?";
        boolean isSuccess = false;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            isSuccess = rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}