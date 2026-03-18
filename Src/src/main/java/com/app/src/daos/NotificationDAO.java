package com.app.src.daos;

import com.app.src.models.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends AbstractDAO<Notification> {

    // 1. Áp dụng Singleton Pattern giống TaskDAO
    private static NotificationDAO instance;

    public static NotificationDAO getInstance() {
        if (instance == null) {
            instance = new NotificationDAO();
        }
        return instance;
    }

    @Override
    public Notification findById(int id) {
        String sql = "SELECT * FROM NOTIFICATION WHERE Not_id = ?";
        Notification notification = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                notification = new Notification();
                notification.setNotiId(rs.getInt("Not_id"));
                notification.setNotiTitle(rs.getString("Not_title"));
                notification.setNotiDescription(rs.getString("Not_description"));
                notification.setNotiIsRead(rs.getBoolean("Not_isRead"));

                if (rs.getTimestamp("Not_date") != null) {
                    notification.setNotiTime(String.valueOf(rs.getTimestamp("Not_date")));
                }
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi tìm Notification theo ID: " + ex.getMessage(), ex);
        }
        return notification;
    }

    // Lấy danh sách thông báo theo UserID
    public List<Notification> findByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM NOTIFICATION WHERE User_id = ? ORDER BY Not_date DESC"; // Thêm ORDER BY để thông báo mới nhất hiện lên trên
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Notification noti = new Notification();
                noti.setNotiId(rs.getInt("Not_id"));
                noti.setNotiTitle(rs.getString("Not_title"));
                noti.setNotiDescription(rs.getString("Not_description"));
                noti.setNotiIsRead(rs.getBoolean("Not_isRead"));

                if (rs.getTimestamp("Not_date") != null) {
                    noti.setNotiTime(String.valueOf(rs.getTimestamp("Not_date")));
                }

                notifications.add(noti);
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Notification theo User ID: " + ex.getMessage(), ex);
        }

        return notifications;
    }

    @Override
    public List<Notification> findAll() {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM NOTIFICATION ORDER BY Not_date DESC";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Notification noti = new Notification();
                noti.setNotiId(rs.getInt("Not_id"));
                noti.setNotiTitle(rs.getString("Not_title"));
                noti.setNotiDescription(rs.getString("Not_description"));
                noti.setNotiIsRead(rs.getBoolean("Not_isRead"));

                if (rs.getTimestamp("Not_date") != null) {
                    noti.setNotiTime(String.valueOf(rs.getTimestamp("Not_date")));
                }

                notifications.add(noti);
            }
            this.closeResource(ps, connection, rs);
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi lấy danh sách Notification: " + ex.getMessage(), ex);
        }
        return notifications;
    }

    @Override
    public boolean create(Notification entity) {
        // Cập nhật SQL khớp với các trường mới
        String sql = "INSERT INTO NOTIFICATION (Not_title, Not_description, Not_isRead, Not_date) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);

            ps.setString(1, entity.getNotiTitle()); // Cần đảm bảo Model có getNotiTitle()
            ps.setString(2, entity.getNotiDescription());
            ps.setBoolean(3, entity.isNotiIsRead());

            // Nếu bạn có trường User_id trong Notification Model, có thể thêm vào đây:
            // ps.setInt(4, entity.getUserId());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi tạo mới Notification: " + ex.getMessage(), ex);
        } finally {
            try {
                this.closeResource(ps, connection, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean update(int id, Notification entity) {
        String sql = "UPDATE NOTIFICATION SET Not_title = ?, Not_description = ?, Not_isRead = ? WHERE Not_id = ?";
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);

            ps.setString(1, entity.getNotiTitle());
            ps.setString(2, entity.getNotiDescription());
            ps.setBoolean(3, entity.isNotiIsRead());
            ps.setInt(4, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi cập nhật Notification: " + ex.getMessage(), ex);
        } finally {
            try {
                this.closeResource(ps, connection, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM NOTIFICATION WHERE Not_id = ?";
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi xóa Notification: " + ex.getMessage(), ex);
        } finally {
            try {
                this.closeResource(ps, connection, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Phương thức bổ sung: Đánh dấu tất cả thông báo của 1 user là đã đọc
    public boolean markAllAsRead(int userId) {
        String sql = "UPDATE NOTIFICATION SET Not_isRead = true WHERE User_id = ?";
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = getConnection();
            ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Lỗi khi đánh dấu đã đọc Notification: " + ex.getMessage(), ex);
        } finally {
            try {
                this.closeResource(ps, connection, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}