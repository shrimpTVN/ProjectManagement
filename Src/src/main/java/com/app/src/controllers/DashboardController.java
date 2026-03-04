package com.app.src.controllers;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;


public class DashboardController implements Initializable {

    @FXML
    private StackPane contentArea; // From your dashboard.fxml <center>

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Register the content area with the Navigator
        ViewNavigator.getInstance().setMainContentArea(contentArea);
        // 2. Load the default view when the app starts -> load home view
        ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
    }
}