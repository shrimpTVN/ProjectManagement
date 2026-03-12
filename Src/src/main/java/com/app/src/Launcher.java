package com.app.src;

import com.app.src.exceptions.GlobalExceptionHandler;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        GlobalExceptionHandler.registerDefaultHandler();

        Application.launch(ProjectManagementApplication.class, args);
    }
}
