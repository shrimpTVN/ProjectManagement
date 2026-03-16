package com.app.src;

import com.app.src.controllers.SceneManager;
import com.app.src.core.AppContext;
import com.app.src.core.session.UserSession;
import com.app.src.services.UserService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ProjectManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        stage.setTitle("Project Management Application");

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initStage(stage);
        sceneManager.switchScene("/scenes/login.fxml");

//        try {
//            loadExampleData();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }


    }
    private void loadExampleData() throws SQLException {
        SceneManager sceneManager = SceneManager.getInstance();
        UserService userService = new UserService();
        UserSession.getInstance().setUser(userService.getUserById(3)); //id=3 -> van nghia
        AppContext.getInstance();
        sceneManager.switchScene("/scenes/dashboard.fxml");
    }
}
