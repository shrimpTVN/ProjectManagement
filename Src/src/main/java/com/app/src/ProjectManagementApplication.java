package com.app.src;

import com.app.src.controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.io.IOException;

public class ProjectManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {






        stage.setTitle("Project Management Application");

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initStage(stage);
        sceneManager.switchScene("/scenes/login.fxml");
//        sceneManager.switchScene("/scenes/dashboard.fxml");



//        load the FXML file from the classpath (src/main/resources)
//        URL fxmlUrl = LoginApplication.class.getResource("/scenes/login.fxml");
//        if (fxmlUrl == null) {
//            throw new IllegalStateException("FXML resource not found ");
//        }
//        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
//
//        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
//        stage.setTitle("Login");
//        stage.setScene(scene);
//        stage.show();

    }
}
