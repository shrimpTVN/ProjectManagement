package com.app.src.controllers.notification;

import com.app.src.models.Notification;
import com.app.src.models.User;
import com.app.src.services.NotificationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;
import com.app.src.core.AppContext;

public class NotificationController {

    @FXML
    private VBox notificationsContainer;

    private NotificationService notificationService;
    private User currentUser;

    /**
     * Hàm initialize() được JavaFX tự động gọi sau khi file FXML đã được load xong các thành phần UI.
     */
    @FXML
    public void initialize() {
        // 1. Khởi tạo service (Bạn cần đảm bảo NotificationService có constructor rỗng hoặc singleton)
        this.notificationService = new NotificationService();

        // 2. Lấy user hiện tại từ AppContext
        this.currentUser = AppContext.getInstance().getUserData();

        // 3. Tự động load dữ liệu nếu đã có user
        if (currentUser != null) {
            loadNotificationsList(currentUser.getUserId());
        } else {
            System.err.println("Cảnh báo: Không tìm thấy thông tin User trong AppContext.");
        }
    }

    public void loadNotificationsList(int userId) {
        // Clear danh sách cũ trước khi render mới
        System.out.println("Chay được Loader");
        notificationsContainer.getChildren().clear();

        try {
            List<Notification> notificationList = notificationService.getNotificationsByUserId(userId);

            if (notificationList == null || notificationList.isEmpty()) {
                System.out.println("Thông báo: Không có thông báo nào cho người dùng này.");
                return;
            }

            for (Notification noti : notificationList) {
                try {
                    // Đảm bảo đường dẫn FXML chính xác (nên bắt đầu bằng /com/app/...)
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/NotificationItem.fxml"));
                    Node itemNode = loader.load();

                    // Lấy controller của item và truyền dữ liệu
                    NotificationItemController itemController = loader.getController();
                    itemController.setData(noti);

                    // Đưa item vào container
                    notificationsContainer.getChildren().add(itemNode);

                } catch (IOException e) {
                    System.err.println("Lỗi khi load NotificationItem FXML: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy dữ liệu từ Service: " + e.getMessage());
            e.printStackTrace();
        }
    }
}