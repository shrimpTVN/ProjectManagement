package com.app.src.controllers;

import com.app.src.services.UserService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

public class SignUpController {

    @FXML
    private TextField usernameInput;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField phoneInput;
    @FXML
    private DatePicker dobInput;
    @FXML
    private ToggleGroup gender;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private PasswordField passwordAgainInput;

    @FXML
    private Label labelMess;

    @FXML
    public void goToLogin(javafx.scene.input.MouseEvent event) {
        ViewNavigator.getInstance().loadSubScene("/scenes/login.fxml");
    }

    @FXML
    public void handleSignUp(ActionEvent event) {
        // Reset thông báo mỗi lần ấn nút
        labelMess.setText("");

        String username = usernameInput.getText();
        String name = nameInput.getText();
        String phone = phoneInput.getText();
        String dob = dobInput.getValue() != null ? dobInput.getValue().toString() : "";

        String selectedGender = "";
        if (gender.getSelectedToggle() != null) {
            RadioButton selectedRadioButton = (RadioButton) gender.getSelectedToggle();
            selectedGender = selectedRadioButton.getText();
        }

        String password = passwordInput.getText();
        String passwordAgain = passwordAgainInput.getText();

        if (username.isBlank() || name.isBlank() || phone.isBlank() || dob.isBlank() || selectedGender.isBlank() || password.isBlank()) {
            showLabelMessage("Vui lòng điền đầy đủ thông tin.", "red");
            return;
        }

        if (!password.equals(passwordAgain)) {
            showLabelMessage("Mật khẩu nhập lại không khớp.", "red");
            return;
        }

        try {
            UserService userService = new UserService();
            boolean isSuccess = userService.register(username, name, phone, dob, selectedGender, password);

            if (isSuccess) {
                showLabelMessage("Đăng ký thành công! Đang chuyển hướng...", "#84D18D");

                // Đợi 1.5 giây rồi chuyển về màn hình đăng nhập
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> {
                    ViewNavigator.getInstance().loadSubScene("/scenes/login.fxml");
                });
                pause.play();

            } else {
                showLabelMessage("Đăng ký thất bại. Tên đăng nhập hoặc SĐT đã tồn tại.", "red");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showLabelMessage("Lỗi hệ thống: Không thể kết nối cơ sở dữ liệu.", "red");
        }
    }

    // Hàm hỗ trợ set text và màu
    private void showLabelMessage(String message, String color) {
        labelMess.setText(message);
        labelMess.setStyle("-fx-text-fill: " + color + "; -fx-font-family: 'Urbanist'; -fx-font-weight: bold;");
    }
}