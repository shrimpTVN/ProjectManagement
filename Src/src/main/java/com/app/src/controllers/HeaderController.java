package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.awt.event.ActionEvent;

public class HeaderController {
    @FXML
    private HBox appLogo;

    public void handleAppLogoClick(MouseEvent mouseEvent) {
        ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
    }
}
