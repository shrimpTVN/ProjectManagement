package com.app.src.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class ViewNavigator {
    private static ViewNavigator instance;

    // This is the <center> area of your Dashboard
    private StackPane mainContentArea;

    private ViewNavigator() { }

    public static ViewNavigator getInstance() {
        if (instance == null) {
            instance = new ViewNavigator();
        }
        return instance;
    }

    // The Dashboard will call this once to register its center area
    public void setMainContentArea(StackPane contentArea) {
        this.mainContentArea = contentArea;
    }

    /**
     * Loads an FXML file into the center dashboard area.
     * Returns the controller so you can pass data to it if needed.
     */
    public <T> T loadSubScene(String fxmlPath) {
        try {
//          load FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();

            // Clear old view and set new view
            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(view);

            // Return the controller for data passing
            return loader.getController();

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
}