package com.app.src.controllers.task;

import com.app.src.models.StatusUpdating;
import com.app.src.utils.TimeAgoHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class StatusHistoryController {
    @FXML
    AnchorPane statusChangeItemRoot;

    @FXML private Label lblUser,lblAction, lblTime, lblStatusChange;

    public void setData(StatusUpdating data) {
        if (data == null || data.getContent() == null) return;

        // 1. TÁCH CHUỖI: parts[0] là tên, parts[1] là nội dung thay đổi
        String[] parts = data.getContent().split("\\|");

        if (parts.length >= 2) {
            // Gán tên người dùng vào lblUser
            lblUser.setText(parts[0]);

            // Gán phần "Cũ -> Mới" vào lblStatusChange
            lblStatusChange.setText(parts[1]);
        }

        // Các phần còn lại giữ nguyên
        lblAction.setText(" changed the status");
        lblUser.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: black;");
        lblAction.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");

        String timeAgo = TimeAgoHelper.toTimeAgo(data.getDate());
        lblTime.setText(timeAgo);
        lblTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
        lblStatusChange.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

    }
    public void renderData(int taskId){

            System.out.println("Rendering task status history for: " + taskId);

    }
}
