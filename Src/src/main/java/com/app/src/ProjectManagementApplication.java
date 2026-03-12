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

<<<<<<< HEAD

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


        stage.setTitle("Project Management Application");

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initStage(stage);

        sceneManager.switchScene("/scenes/login.fxml");
=======
            sceneManager.switchScene("/scenes/login.fxml");
>>>>>>> a1ecd732348f93149a2ef5e7bdc25f8f8f6d85f9
//        sceneManager.switchScene("/scenes/dashboard.fxml");




        } catch (Exception exception) {
            GlobalExceptionHandler.handle(exception);
            throw exception;
        }
    }
}
