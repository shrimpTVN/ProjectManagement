package com.app.src;

import com.app.src.controllers.SceneManager;
import com.app.src.core.AppContext;
import com.app.src.core.session.UserSession;
import com.app.src.services.UserService;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ProjectManagementApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Danh sách các font chữ Urbanist hay dùng trong giao diện
        String[] fontFiles = {
                "/Font/Urbanist-Regular.ttf",
                "/Font/Urbanist-Medium.ttf",
                "/Font/Urbanist-SemiBold.ttf",
                "/Font/Urbanist-Bold.ttf",
                "/Font/Urbanist-ExtraBold.ttf"
        };

        // Nạp font chữ vào hệ thống
        for (String fontFile : fontFiles) {
            try {
                Font.loadFont(getClass().getResourceAsStream(fontFile), 14);
            } catch (Exception e) {
                System.out.println("Không load được font: " + fontFile);
            }
        }

        stage.setTitle("Project Management Application");
        System.out.println("Families: " + Font.getFamilies());
        System.out.println("Font Names: " + Font.getFontNames());

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
        // Load AuthWrapper làm màn hình gốc
        sceneManager.switchScene("/scenes/AuthWrapper.fxml");
    }
}