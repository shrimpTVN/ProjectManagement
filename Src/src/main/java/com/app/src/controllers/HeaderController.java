package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.awt.event.ActionEvent;

public class HeaderController {
    @FXML
    private HBox appLogo;

    @FXML
    private ImageView User_Account;


    public void handleAppLogoClick(MouseEvent mouseEvent) {
        ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
    }


    @FXML
    public void handleUserAccountClick(MouseEvent event) {
        System.out.println("Đã click vào icon Account!"); // Dòng này để kiểm tra
        // Reset sidebar active states
        if (SideBarController.getInstance() != null) {
            SideBarController.getInstance().resetActive();
        }
        // Load the information scene and get its controller
        InformationController infoController = ViewNavigator.getInstance().loadSubScene("/scenes/information.fxml");
        System.out.println("Information controller loaded: " + (infoController != null));
    }
}
