package com.app.src;

import com.app.src.authentication.RoleValidator;
import com.app.src.authentication.VisibleManer;
import com.app.src.controllers.SceneManager;
import com.app.src.core.async.AsyncExecutor;
import com.app.src.core.service.chat.ChatClientService;
import com.app.src.exceptions.GlobalExceptionHandler;
import com.app.src.core.AppContext;
import com.app.src.core.session.UserSession;
import com.app.src.services.UserService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ProjectManagementApplication extends Application {
    private ChatClientService chatService;

    @Override
    public void start(Stage stage) throws IOException {
        GlobalExceptionHandler.registerDefaultHandler();

        stage.setTitle("Project Management Application");

        SceneManager sceneManager = SceneManager.getInstance();
        sceneManager.initStage(stage);

        // Load AuthWrapper làm màn hình gốc
        sceneManager.switchScene("/scenes/AuthWrapper.fxml");

        loadSessionData();

        // 3. Khởi tạo các Service cần thiết
        chatService = ChatClientService.getInstance();

    }

    @Override
    public void stop() throws Exception {
        System.out.println("Đang tắt ứng dụng và dọn dẹp tài nguyên...");

        // 1. Dừng Chat Listener một cách duyên dáng (Graceful shutdown)
        if (chatService != null) {
            chatService.disconnect();
        }

        // 2. Đóng Thread Pool
        AsyncExecutor.getInstance().shutdown();

        super.stop();
    }

    private void loadSessionData(){

        RoleValidator.getInstance();
        VisibleManer.getInstance();
    }

    private void loadExampleData() throws SQLException, IOException {
        SceneManager sceneManager = SceneManager.getInstance();
        UserService userService = new UserService();
        UserSession.getInstance().setUser(userService.getUserById(3)); //id=3 -> van nghia

        AppContext.getInstance();
        sceneManager.switchScene("/scenes/dashboard.fxml");

        // Có thể gọi connect ở đây hoặc đợi user login xong
        chatService.connectDefault();
    }
}
