package com.app.src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
//        load the FXML file from the classpath (src/main/resources)
        URL fxmlUrl = LoginApplication.class.getResource("/scenes/login.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("FXML resource not found ");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
