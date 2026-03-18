package com.app.src.services;
//package com.app.src.services;

import com.app.src.daos.NotificationDAO;
import com.app.src.models.Notification;
import java.util.List;

public class NotificationService {

    private NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }
//Hàm lấy thông báo theo user
    public List<Notification> getNotificationsByUserId(int userId) {
        // Bạn có thể thêm các logic kiểm tra (validation) ở đây nếu cần
        if (userId <= 0) {
            System.out.println("[DEBUG - NotificationService] Lỗi: userId không hợp lệ!");
            return null;
        }
        return notificationDAO.findByUserId(userId);
    }
//Hàm đánh dấu đã đọc
    public boolean markAsRead(int notiId, Notification noti) {
        noti.setNotiIsRead(true);
        return notificationDAO.update(notiId, noti);
    }
}
