package com.app.src.controllers;

import javafx.fxml.FXML;

public class SignUpController {

    @FXML
    public void goToLogin(javafx.scene.input.MouseEvent event) {
        ViewNavigator.getInstance().loadSubScene("/scenes/login.fxml");
    }
}