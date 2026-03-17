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
        lblUser.setText("Oitloc96");
        lblAction.setText("changed the status");
        lblUser.setStyle("--fx-font-size: 12px;-fx-font-weight: bold; -fx-text-fill: black");
        lblAction.setStyle("-fx-text-fill: black;-fx-font-size: 12px;");
        // Sử dụng Helper để hiển thị "8 phút trước"
        // Hiển thị thời gian
        String timeAgo = TimeAgoHelper.toTimeAgo(data.getDate());
        lblTime.setText(timeAgo);
        // Quan trọng: Ép màu và font size lớn hơn một chút để dễ nhìn
        lblTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #888888;");
        // Hiển thị chuỗi "None ➔ To Do" hoặc "To Do ➔ In Progressing"
        lblStatusChange.setText(data.getContent());
        lblStatusChange.setStyle(" -fx-font-size: 12px; -fx-text-fill: black;");

    }
    public void renderData(int taskId){

            System.out.println("Rendering task status history for: " + taskId);

    }
}
