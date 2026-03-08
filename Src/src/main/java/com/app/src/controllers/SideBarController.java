package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SideBarController implements Initializable {

    @FXML
    private Button addProjectBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Gán sự kiện click cho nút dấu +
        addProjectBtn.setOnAction(event -> openCreateProject());
    }

    private void openCreateProject() {
        try {
            // Tải giao diện tạo dự án
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/CreateProject.fxml"));
            Parent createProjectView = loader.load();

            // Lấy BorderPane gốc của màn hình
            BorderPane mainLayout = (BorderPane) addProjectBtn.getScene().getRoot();

            // Lấy StackPane contentArea đang nằm ở vị trí center
            StackPane contentArea = (StackPane) mainLayout.getCenter();

            // Xóa nội dung cũ và nạp giao diện tạo dự án vào
            contentArea.getChildren().clear();
            contentArea.getChildren().add(createProjectView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}