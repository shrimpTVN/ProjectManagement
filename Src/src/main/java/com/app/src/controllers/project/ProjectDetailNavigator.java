package com.app.src.controllers.project;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ProjectDetailNavigator {
    private static ProjectDetailNavigator instance;
    private StackPane mainContentArea;

    private ProjectDetailNavigator() {
    }

    public static ProjectDetailNavigator getInstance() {
        if (instance == null){
            instance = new ProjectDetailNavigator();
        }
        return instance;
    }

    public void setMainContentArea(Node contentArea) {
        this.mainContentArea = (StackPane) contentArea;
    }

    public <T> T loadSubView(String fxmlPath)  {
       try {
           FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
           Node subView = loader.load();

           mainContentArea.getChildren().clear();
           mainContentArea.getChildren().add(subView);

           return loader.getController();
       } catch (Exception e) {
           System.out.println("Error while loading sub view"+ e.getMessage());
           e.printStackTrace();
           return null;
       }
    }
}
