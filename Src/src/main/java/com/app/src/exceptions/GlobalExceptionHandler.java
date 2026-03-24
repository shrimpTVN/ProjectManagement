package com.app.src.exceptions;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    public GlobalExceptionHandler() {
    }

    public static void registerDefaultHandler() {
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler::handleUncaughtException);
    }

    public static void handle(Throwable throwable) {
        Thread thread = Thread.currentThread();
        logException(thread, throwable);
        showErrorDialog(thread, throwable);
    }

    private static void handleUncaughtException(Thread thread, Throwable throwable) {
        logException(thread, throwable);
        showErrorDialog(thread, throwable);
    }

    private static void logException(Thread thread, Throwable throwable) {
        String stackTrace = toStackTrace(throwable);
        System.err.println("[GLOBAL-ERROR] thread=" + thread.getName());
        System.err.println(stackTrace);
    }

    private static void showErrorDialog(Thread thread, Throwable throwable) {
        Runnable task = () -> {
            AppException appException = mapToAppException(throwable);
            String stackTrace = toStackTrace(throwable);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("An error occurred during processing");
            alert.setContentText(
                    "ErrorCode: " + appException.getErrorCode().getCode() + "\n" +
                    "Message: " + appException.getMessage() + "\n" +
                    "Thread: " + thread.getName());

            TextArea stackTraceArea = new TextArea(stackTrace);
            stackTraceArea.setEditable(false);
            stackTraceArea.setWrapText(false);
            VBox.setVgrow(stackTraceArea, Priority.ALWAYS);
            alert.getDialogPane().setExpandableContent(stackTraceArea);
            alert.getDialogPane().setExpanded(true);

            alert.show();
        };

        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            Platform.runLater(task);
        }
    }

    private static AppException mapToAppException(Throwable throwable) {
        if (throwable instanceof AppException appException) {
            return appException;
        }

        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            message = ErrorCode.UNKNOWN_ERROR.getDefaultMessage();
        }

        return new AppException(ErrorCode.UNKNOWN_ERROR, message, throwable);
    }

    private static String toStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}

