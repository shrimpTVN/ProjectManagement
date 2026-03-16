package com.app.src.controllers.task;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class StatusHistoryController {
    @FXML
    AnchorPane statusChangeItemRoot;

    public void renderData(int taskId){

            System.out.println("Rendering task status history for: " + taskId);

    }
}
