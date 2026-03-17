package com.app.src.controllers;

import com.app.src.services.UserService;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

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

    // Thêm biến cho nút Sign Up
    @FXML
    private Button signUpBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Khóa nhập tay, chỉ cho phép click chọn lịch
        dobInput.setEditable(false);
        // Gán phím Enter cho nút Đăng ký
        signUpBtn.setDefaultButton(true);
    }

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

        // 1. Gom tên các ô bị trống vào danh sách
        List<String> missingFields = new ArrayList<>();
        if (username.isBlank()) missingFields.add("Username");
        if (name.isBlank()) missingFields.add("Name");
        if (phone.isBlank()) missingFields.add("Telephone");
        if (dob.isBlank()) missingFields.add("Date of birth");
        if (selectedGender.isBlank()) missingFields.add("Sex");
        if (password.isBlank()) missingFields.add("Password");
        if (passwordAgain.isBlank()) missingFields.add("Password again");

        // 2. Xử lý hiển thị thông báo thiếu dữ liệu
        if (!missingFields.isEmpty()) {
            if (missingFields.size() == 7) {
                showLabelMessage("Please fill in all fields.", "red");
            } else {
                // Nối các tên ô trống lại bằng dấu phẩy
                showLabelMessage("Missing: " + String.join(", ", missingFields), "red");
            }
            return;
        }

        // 3. Kiểm tra tính hợp lệ (chỉ chạy khi các ô đã có dữ liệu)
        String phoneRegex = "^0[35789]\\d{8}$";
        if (!phone.matches(phoneRegex)) {
            showLabelMessage("Invalid phone number. Must be 10 digits (VN format).", "red");
            return;
        }

        if (!password.equals(passwordAgain)) {
            showLabelMessage("Passwords do not match.", "red");
            return;
        }

        try {
            UserService userService = new UserService();
            boolean isSuccess = userService.register(username, name, phone, dob, selectedGender, password);

            if (isSuccess) {
                showLabelMessage("Sign up successful! Redirecting...", "#84D18D");

                // Đợi 1.5 giây rồi chuyển về màn hình đăng nhập
                PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
                pause.setOnFinished(e -> {
                    ViewNavigator.getInstance().loadSubScene("/scenes/login.fxml");
                });
                pause.play();

            } else {
                showLabelMessage("Sign up failed. Username or phone already exists.", "red");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showLabelMessage("System error. Cannot connect to database.", "red");
        }
    }

    // Hàm hỗ trợ set text và màu
    private void showLabelMessage(String message, String color) {
        labelMess.setText(message);
        labelMess.setStyle("-fx-text-fill: " + color + "; -fx-font-family: 'Urbanist Bold';");
    }
}