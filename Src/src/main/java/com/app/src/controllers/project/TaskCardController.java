package com.app.src.controllers.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TaskCardController {

    @FXML
    private Label taskNameLabel;

    @FXML
    private Label assignedToLabel;

    // Hàm này sẽ được gọi từ BoardController để truyền dữ liệu
    public void setData(String taskName, String assigneeName) {
        taskNameLabel.setText(taskName);
        assignedToLabel.setText(assigneeName != null ? assigneeName : "Chưa phân công");
    }
}