package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.event.ActionEvent;


public class HomeController {

    @FXML
    private Button allTasksBtn;

    @FXML
    private Button projectsBtn;

    public void handleProjectBtnClick(javafx.event.ActionEvent actionEvent) {

    ViewNavigator.getInstance().loadSubScene("/scenes/ProjectList.fxml");
    }

    public void handleAllTaskBtnClick(javafx.event.ActionEvent actionEvent) {
        ViewNavigator.getInstance().loadSubScene("/scenes/tasklist.fxml");
    }
}
