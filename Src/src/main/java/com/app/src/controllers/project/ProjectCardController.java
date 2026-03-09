package com.app.src.controllers.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProjectCardController {
    @FXML private Label projectNameLabel;
    @FXML private Label authorLabel;

    public void setData(String projectName, String author) {
        projectNameLabel.setText(projectName);
        authorLabel.setText(author);
    }
}