package com.server.service;

import com.server.dao.NotificationDAO;
import com.server.model.Notification;

import java.util.List;

public class NotificationService {
	private final NotificationDAO notificationDAO = new NotificationDAO();

	public boolean createNotification(Notification notification) {
		return notification != null && notificationDAO.create(notification);
	}

	public Notification getNotificationById(int notificationId) {
		if (notificationId <= 0) {
			return null;
		}
		return notificationDAO.findById(notificationId);
	}

	public List<Notification> getNotificationsByUserId(int userId) {
		if (userId <= 0) {
			return List.of();
		}
		return notificationDAO.findByUserId(userId);
	}

	public List<Notification> getAllNotifications() {
		return notificationDAO.findAll();
	}

	public boolean markAsRead(int notificationId, Notification notification) {
		if (notificationId <= 0 || notification == null) {
			return false;
		}
		notification.setNotiIsRead(true);
		return notificationDAO.update(notificationId, notification);
	}

	public boolean deleteNotification(int notificationId) {
		return notificationId > 0 && notificationDAO.delete(notificationId);
	}
}
