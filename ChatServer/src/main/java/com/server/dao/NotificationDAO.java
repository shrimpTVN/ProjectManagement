package com.server.dao;

import com.server.model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationDAO extends AbstractDAO<Notification> {
	private final AtomicInteger idSequence = new AtomicInteger(1);
	private final Map<Integer, Notification> storage = new ConcurrentHashMap<>();

	@Override
	public Notification findById(int id) {
		return cloneNotification(storage.get(id));
	}

	@Override
	public List<Notification> findAll() {
		List<Notification> notifications = new ArrayList<>(storage.size());
		for (Notification notification : storage.values()) {
			notifications.add(cloneNotification(notification));
		}
		return notifications;
	}

	@Override
	public boolean create(Notification entity) {
		// Cập nhật SQL khớp với các trường mới
		String sql = "INSERT INTO NOTIFICATION (Not_title, Not_description, Not_isRead, Not_date, User_id) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = getConnection();
			ps = connection.prepareStatement(sql);

			ps.setString(1, entity.getNotiTitle()); // Cần đảm bảo Model có getNotiTitle()
			ps.setString(2, entity.getNotiDescription());
			ps.setBoolean(3, false);
			ps.setInt(4, entity.getUserId());

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
	public boolean update(int id, Notification notification) {
		if (notification == null) {
			return false;
		}
		if (!storage.containsKey(id)) {
			return false;
		}
		storage.put(id, cloneNotification(notification));
		return true;
	}

	@Override
	public boolean delete(int id) {
		return storage.remove(id) != null;
	}

	public List<Notification> findByUserId(int userId) {
		if (userId <= 0) {
			return Collections.emptyList();
		}
		List<Notification> notifications = new ArrayList<>();
		for (Notification notification : storage.values()) {
			if (notification.getUserId() == userId) {
				notifications.add(cloneNotification(notification));
			}
		}
		return notifications;
	}

	private Notification cloneNotification(Notification source) {
		if (source == null) {
			return null;
		}
		Notification notification = new Notification();
		notification.setNotiTitle(source.getNotiTitle());
		notification.setNotiDescription(source.getNotiDescription());
		notification.setNotiIsRead(source.isNotiIsRead());
		notification.setNotiTime(source.getNotiTime());
		notification.setUserId(source.getUserId());
		return notification;
	}
}
