package com.app.src.controllers;



// CODE NÀY LÀ DO GEMINI TẠO ĐỂ GẮN SẴN VÀO FILE dashboard.fxml!!!


import com.app.src.utils.MySQLDatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    // --- CÁC BIẾN MENU BÊN TRÁI ---
    @FXML private Label myProjectLabel;
    @FXML private Button addProjectBtn;
    @FXML private VBox projectList;
    @FXML private TextField searchProjectField;

    // --- CÁC BIẾN KHU VỰC TRUNG TÂM ---
    @FXML private Hyperlink homeLink;
    @FXML private Hyperlink dashboardLink;
    @FXML private Label myProjectLabel1;
    @FXML private TextField mainSearchField;
    @FXML private FlowPane recentProjectsFlowPane;
    @FXML private FlowPane allProjectsFlowPane;

    @FXML
    public void initialize() {

        // 1. Đóng/mở menu "> My project"
        myProjectLabel.setOnMouseClicked(event -> {
            boolean isCurrentlyVisible = projectList.isVisible();
            projectList.setVisible(!isCurrentlyVisible);
            projectList.setManaged(!isCurrentlyVisible);

            if (!isCurrentlyVisible) {
                myProjectLabel.setText("v My project");
            } else {
                myProjectLabel.setText("> My project");
            }
        });

        // 2. Nút "+" tạo dự án
        addProjectBtn.setOnAction(event -> {
            System.out.println("Đang mở form tạo Project mới...");
        });

        // 3. Bắt sự kiện thanh tìm kiếm (gõ chữ tới đâu, nhận tới đó)
        mainSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Người dùng đang tìm: " + newValue);
            // Về sau sẽ viết logic lọc danh sách thẻ dự án ở vị trí này
        });

        // 4. Bắt sự kiện bấm vào đường dẫn (Breadcrumb)
        homeLink.setOnAction(e -> System.out.println("Chuyển về trang Home"));
        dashboardLink.setOnAction(e -> System.out.println("Tải lại trang Dashboard"));
    }
}