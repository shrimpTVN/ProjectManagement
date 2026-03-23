package com.app.src.controllers;

import com.app.src.core.async.AsyncExecutor;
import com.app.src.core.service.chat.ChatClientService;
import com.app.src.core.session.UserSession;
import com.app.src.exceptions.AppException;
import com.app.src.models.Notification;
import com.app.src.services.LoginService;
import com.app.src.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.app.src.exceptions.ErrorCode.DATABASE_ERROR;

public class LoginController implements Initializable {

    @FXML
    private TextField userNameInput;
    @FXML
    private PasswordField passwordInput; // Cập nhật thành PasswordField
    @FXML
    private Label labelLoginMess;
    @FXML
    private Button loginBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label welcomeText;
    @FXML
    private BorderPane authContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Gán nút Login làm nút mặc định kích hoạt khi nhấn Enter
        loginBtn.setDefaultButton(true);
    }

    @FXML
    public void goToSignUp(javafx.scene.input.MouseEvent event) {
        ViewNavigator.getInstance().loadSubScene("/scenes/signup.fxml");
    }

    @FXML
    public void handleCancelBtnClick(ActionEvent event) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleLoginBtnClick(ActionEvent event) {
        // Xóa thông báo lỗi cũ mỗi lần bấm đăng nhập
        labelLoginMess.setText("");

        if (userNameInput.getText().isBlank() || passwordInput.getText().isBlank()) {
            labelLoginMess.setText("Type your user name and password!");
        } else {
            try {
                LoginService loginService = new LoginService();
                int userId = loginService.validateLogin(userNameInput.getText(), passwordInput.getText());

                if (userId != -1) {
                    UserService userService = new UserService();
                    UserSession.getInstance().setUser(userService.getUserById(userId));
                    SceneManager.getInstance().switchScene("/scenes/dashboard.fxml");

                    // connection den chat server
                    connectChatServerSafely();

                    String msg = ChatClientService.getInstance().generateNotification("Đăng ký kết nối","", userId);
                    ChatClientService.getInstance().sendMessage(msg);
                } else {
                    labelLoginMess.setText("Invalid username or password!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new AppException(DATABASE_ERROR, "Không thể truy cập dữ liệu để xác thực");
            }
        }
    }

    private void connectChatServerSafely() {
        // Ket noi chat o background thread de khong block JavaFX UI thread.
        AsyncExecutor.getInstance().runAsync(() -> {
            try {
                ChatClientService.getInstance().connectDefault();
            } catch (Exception chatException) {
                System.err.println("[CHAT-WARN] Khong the ket noi chat server: " + chatException.getMessage());
                chatException.printStackTrace();
            }
        });
    }
}