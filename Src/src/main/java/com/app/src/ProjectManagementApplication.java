package com.app.src;

import com.app.src.controllers.SceneManager;
import com.app.src.exceptions.GlobalExceptionHandler;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;

public class ProjectManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        GlobalExceptionHandler.registerDefaultHandler();

        try {

            stage.setTitle("Project Management Application");

            SceneManager sceneManager = SceneManager.getInstance();
            sceneManager.initStage(stage);

            sceneManager.switchScene("/scenes/login.fxml");
//        sceneManager.switchScene("/scenes/dashboard.fxml");




        } catch (Exception exception) {
            GlobalExceptionHandler.handle(exception);
            throw exception;
        }
    }
}
