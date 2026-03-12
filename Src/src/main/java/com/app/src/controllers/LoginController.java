package com.app.src.controllers;

import com.app.src.core.session.UserSession;
import com.app.src.exceptions.AppException;
import com.app.src.models.User;
import com.app.src.services.LoginService;
import com.app.src.services.UserService;
import com.app.src.utils.MySQLDatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.app.src.exceptions.ErrorCode.DATABASE_ERROR;

public class LoginController {
    @FXML
    private TextField userNameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Label labelLoginMess;
    @FXML
    private Button loginBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Label welcomeText;

    //    we need to declare a variable has the same name with the fx:id in the FXML file, and annotate it with @FXML
//    we need add @FXML annotation to the event handler method, so that the FXML loader can access it, and add onMouseClicked="#btnLoginHandle" in the button element
    @FXML
    public void handleCancelBtnClick(ActionEvent event) {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleLoginBtnClick(ActionEvent event) throws SQLException {
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
                } else {
                    labelLoginMess.setText("Invalid username or password!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new AppException(DATABASE_ERROR, "Không thể truy cập dữ liệu để xác thực");
            }
        }
    }
}
