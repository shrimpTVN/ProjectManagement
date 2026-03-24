package com.app.src.controllers.notification;

import com.app.src.models.Notification;
import com.app.src.services.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

public class NotificationItemController {

    // Đã đổi itemRoot thành itemContainer cho khớp 100% với fx:id trong NotificationItem.fxml
    @FXML private HBox itemContainer;
    @FXML private Label titleLabel;
    @FXML private Label descLabel;
    @FXML private Label timeLabel;

    // Khai báo thêm biến này vì trong FXML bạn có thẻ <Label fx:id="statusIcon" ... />
    @FXML private Label statusIcon;

    private Notification notification;
    private NotificationController parentController;

    public void setData(Notification noti) {
        this.notification = noti;

        // Gán text
        titleLabel.setText(noti.getNotiTitle());
        descLabel.setText(noti.getNotiDescription());

        // Format ngày giờ
        if (noti.getNotiTime() != null && !noti.getNotiTime().trim().isEmpty()) {
            try {
                Timestamp ts = Timestamp.valueOf(noti.getNotiTime());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                timeLabel.setText(sdf.format(ts));
            } catch (IllegalArgumentException e) {
                timeLabel.setText(noti.getNotiTime());
            }
        } else {
            timeLabel.setText("");
        }

        // Xử lý UI cho trạng thái đọc / chưa đọc
        if (!noti.isNotiIsRead()) {
            // Chưa đọc: Nền xanh nhạt, chữ đậm, 1 tick xám
            itemContainer.setStyle("-fx-background-color: #E6F2FF;");
            titleLabel.setStyle("-fx-font-weight: bold;");
            statusIcon.setText("✓");
            statusIcon.setStyle("-fx-text-fill: #888888; -fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            // Đã đọc: Nền trắng, chữ bình thường, 2 tick xanh
            itemContainer.setStyle("-fx-background-color: #FFFFFF;");
            titleLabel.setStyle("-fx-font-weight: normal;");
            statusIcon.setText("✓✓");
            statusIcon.setStyle("-fx-text-fill: #4da6ff; -fx-font-size: 18px; -fx-font-weight: bold;");
        }
    }

    public void setParentController(NotificationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleItemClicked() {
        if (notification == null || notification.isNotiIsRead()) {
            return;
        }

        boolean ok = NotificationService.markAsRead(notification.getNotiId(), notification);
        if (ok) {
            notification.setNotiIsRead(true);
            if (parentController != null) {
                parentController.refreshNotifications();
            }
        } else {
            System.err.println("Không thể cập nhật trạng thái đã đọc cho thông báo ID=" + notification.getNotiId());
        }
    }
}