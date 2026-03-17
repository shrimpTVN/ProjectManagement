package com.app.src;

import com.app.src.controllers.SceneManager;
import com.app.src.exceptions.GlobalExceptionHandler;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

public class ProjectManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        GlobalExceptionHandler.registerDefaultHandler();

        try {
            loadAppFonts();

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

    private void loadAppFonts() {
        loadFont("/Font/Urbanist-Regular.ttf");
        loadFont("/Font/Urbanist-Medium.ttf");
        loadFont("/Font/Urbanist-Light.ttf");
        loadFont("/Font/Urbanist-Bold.ttf");
    }

    private void loadFont(String resourcePath) {
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                Font.loadFont(inputStream, 14);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot load font: " + resourcePath, e);
        }
    }
}
