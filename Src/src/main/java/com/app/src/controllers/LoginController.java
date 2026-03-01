package com.app.src.controllers;

import com.app.src.services.LoginService;
import com.app.src.utils.MySQLDatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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
    public void handleCancelBtnClick(ActionEvent event)
    {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleLoginBtnClick(ActionEvent event)
    {
        if (!userNameInput.getText().isBlank() && passwordInput.getText().isBlank())
        {
            labelLoginMess.setText("Type your user name and password!");
        } else {
            MySQLDatabaseConnection connection = new MySQLDatabaseConnection();
            LoginService loginService = new LoginService(connection.getConnection());
            ResultSet queryResult = loginService.validateLogin(userNameInput.getText(), passwordInput.getText() );

           try{
               while (queryResult.next())
               {
                   if (queryResult.getInt(1) == 1)
                   {
                       labelLoginMess.setText("Congratulation! You are login!");
                   }   else{
                       labelLoginMess.setText("Username or Password wrong. Try again!");
                   }

               }
           } catch(Exception e){
               e.printStackTrace();
           }


        }
    }
}
