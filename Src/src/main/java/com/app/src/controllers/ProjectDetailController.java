package com.app.src.controllers;

import com.app.src.models.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class ProjectDetailController {

    @FXML private Label label;

    public void renderData(Project project, String adminName)
    {
        System.out.println("ProjectDetailController ------" + adminName + "    " + project.getProjectName());
        label.setText("This is the project view of " + project.getProjectName());
    }
}
