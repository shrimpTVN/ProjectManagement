package com.app.src.controllers.task;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CommentBoxController {
    @FXML
    AnchorPane commentItemRoot;

    public void renderData(int taskId){

            System.out.println("Rendering task comment for: " + taskId);

    }
}
