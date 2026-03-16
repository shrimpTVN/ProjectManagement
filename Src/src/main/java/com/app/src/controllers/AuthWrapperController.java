package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthWrapperController implements Initializable {
    @FXML
    private StackPane authContentArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Đăng ký khung chứa với ViewNavigator
        ViewNavigator.getInstance().setMainContentArea(authContentArea);

        // Nạp màn hình đăng nhập làm mặc định
        ViewNavigator.getInstance().loadSubScene("/scenes/login.fxml");
    }
}