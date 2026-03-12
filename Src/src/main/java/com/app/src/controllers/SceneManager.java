package com.app.src.controllers;

import com.app.src.exceptions.ErrorCode;
import com.app.src.exceptions.GlobalExceptionHandler;
import com.app.src.exceptions.ServiceException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static SceneManager instance;
    private Stage primaryStage;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void initStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void switchScene(String fxmlPath) {
        try {
            // Load FXML and create scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            ServiceException exception = new ServiceException(
                    ErrorCode.SCENE_LOAD_ERROR,
                    "Error loading scene: " + fxmlPath,
                    e
            );
            GlobalExceptionHandler.handle(exception);
            throw exception;
        }
    }
}
