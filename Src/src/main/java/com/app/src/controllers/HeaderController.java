package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class HeaderController {

    @FXML
    public void handleAppLogoClick(MouseEvent event) {
        ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
    }

    @FXML
    public void handleNotificationClick(MouseEvent event) {
        if (SideBarController.getInstance() != null) {
            SideBarController.getInstance().resetActive();
        }
        ViewNavigator.getInstance().loadSubScene("/scenes/Notification.fxml");
    }

    @FXML
    public void handleUserAccountClick(MouseEvent event) {
        if (SideBarController.getInstance() != null) {
            SideBarController.getInstance().resetActive();
        }
        InformationController infoController = ViewNavigator.getInstance().loadSubScene("/scenes/information.fxml");
        System.out.println("Information controller loaded: " + (infoController != null));
    }
}
