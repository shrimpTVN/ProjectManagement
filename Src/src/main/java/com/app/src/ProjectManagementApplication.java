package com.app.src;

import com.app.src.controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ProjectManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("Project Management Application");

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initStage(stage);
        sceneManager.switchScene("/scenes/login.fxml");
//        sceneManager.switchScene("/scenes/dashboard.fxml");

    }
}
