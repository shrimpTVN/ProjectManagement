package com.app.src.services;
//package com.app.src.services;

import com.app.src.daos.NotificationDAO;
import com.app.src.models.Notification;
import java.util.List;

public class NotificationService {

    private static NotificationDAO notificationDAO;
    private static NotificationService instance;

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
         return instance;
    }
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }
    public static List<Notification> getNotificationsByUserId(int userId) {
        // Bạn có thể thêm các logic kiểm tra (validation) ở đây nếu cần
        if (userId <= 0) {
            System.out.println("[DEBUG - NotificationService] Lỗi: userId không hợp lệ!");
            return null;
        }
        System.out.println("Goi NotificationService thanh cong");
        return notificationDAO.findByUserId(userId);
    }

    //Hàm đánh dấu đã đọc
    public static boolean markAsRead(int notiId, Notification noti) {
        noti.setNotiIsRead(true);
        return notificationDAO.update(notiId, noti);
    }

    public static boolean createNotification(Notification notification)
    {
            return notificationDAO.create(notification);
    }
}
