package com.app.src.controllers;

import com.app.src.models.Notification;
import com.app.src.services.NotificationService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.List;

public class NotificationController {

    @FXML
    private TableView<Notification> tableNotifications;

    @FXML
    private TableColumn<Notification, String> colDescription;

    @FXML
    private TableColumn<Notification, String> colStatus;

    private NotificationService notificationService;
    private int currentUserId; // Biến lưu user đang đăng nhập

    @FXML
    public void initialize() {
        notificationService = new NotificationService();
        setupTableColumns();
    }

    // Cài đặt cách hiển thị dữ liệu cho các cột
    private void setupTableColumns() {
        // Cột Nội dung
        colDescription.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNotiDescription())
        );

        // Cột Trạng thái (Chuyển boolean thành chữ cho user dễ hiểu)
        colStatus.setCellValueFactory(cellData -> {
            boolean isRead = cellData.getValue().isNotiIsRead();
            String statusText = isRead ? "Đã đọc" : "Chưa đọc";
            return new SimpleStringProperty(statusText);
        });
    }

    // Hàm này sẽ được gọi từ màn hình trước (ví dụ Login) để truyền userId sang
    public void loadData(int userId) {
        this.currentUserId = userId;
        loadNotificationsToTable();
    }

    // Hàm gọi Service và đổ dữ liệu lên TableView
    private void loadNotificationsToTable() {
        System.out.println("[DEBUG] Đang tải thông báo cho User ID: " + currentUserId);

        List<Notification> notiList = notificationService.getNotificationsByUserId(currentUserId);

        if (notiList != null && !notiList.isEmpty()) {
            // Đổ dữ liệu vào bảng
            tableNotifications.getItems().setAll(notiList);
            System.out.println("[DEBUG] Tải thành công " + notiList.size() + " thông báo.");
        } else {
            tableNotifications.getItems().clear();
            System.out.println("[DEBUG] Không có thông báo nào hoặc có lỗi xảy ra.");
        }
    }
}