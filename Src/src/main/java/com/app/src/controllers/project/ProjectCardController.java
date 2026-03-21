package com.app.src.controllers.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProjectCardController {
    @FXML private Label projectNameLabel;
    @FXML private Label authorLabel;
    @FXML private Label roleNameLabel;

    public void setData(String projectName, String roleName,  String author) {
        projectNameLabel.setText(projectName);
        roleNameLabel.setText("You're "+roleName);
        authorLabel.setText(author);
    }
}