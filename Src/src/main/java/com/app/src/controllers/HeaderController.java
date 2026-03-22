package com.app.src.controllers;

import com.app.src.controllers.notification.NotificationController;
import com.app.src.core.service.chat.ChatClientService;
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
        NotificationController controller = ViewNavigator.getInstance().loadSubScene("/scenes/Notification.fxml");
        ChatClientService.getInstance().setNotificationController(controller);
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
