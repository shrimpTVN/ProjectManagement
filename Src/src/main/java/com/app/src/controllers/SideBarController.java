package com.app.src.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SideBarController implements Initializable {

    private static SideBarController instance;

    @FXML
    private Button addProjectBtn;

    @FXML
    private VBox projectListContainer;

    @FXML
    private HBox forMeMenu;

    @FXML
    private ImageView forMeIcon;

    @FXML
    private Label forMeLabel;

    @FXML
    private HBox myProjectMenu;

    @FXML
    private ImageView myProjectIcon;

    @FXML
    private Label myProjectLabel;

    private boolean activeForMe = false;
    private boolean activeMyProject = false;

    public static SideBarController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        // Gán sự kiện click cho nút dấu +
        addProjectBtn.setOnAction(event -> openCreateProject());

        // Sự kiện click cho forMeMenu
        forMeMenu.setOnMouseClicked(event -> {
            activeForMe = true;
            activeMyProject = false;
            updateStyles();
            projectListContainer.setVisible(false); // Ẩn danh sách project
            handleForMeClick();
        });


        // Sự kiện click cho myProjectMenu sẽ được xử lý trong handleToggleProjectMenu

        // Mặc định active For Me khi khởi tạo
        activeForMe = true;
        updateStyles();
        projectListContainer.setVisible(false);
    }

    private void openCreateProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/CreateProject.fxml"));
            Parent createProjectView = loader.load();

            BorderPane mainLayout = (BorderPane) addProjectBtn.getScene().getRoot();
            StackPane contentArea = (StackPane) mainLayout.getCenter();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(createProjectView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleForMeClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/Home.fxml"));
            Parent homeView = loader.load();

            BorderPane mainLayout = (BorderPane) addProjectBtn.getScene().getRoot();
            StackPane contentArea = (StackPane) mainLayout.getCenter();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(homeView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStyles() {
        if (activeForMe) {
            // Apply active style instantly
            forMeMenu.setBackground(new Background(new BackgroundFill(Color.web("#4896FE"), new CornerRadii(5), null)));
            forMeLabel.setTextFill(Color.WHITE);
            forMeIcon.setImage(new Image(getClass().getResource("/image/user_white.png").toExternalForm()));
        } else {
            // Apply inactive style instantly
            forMeMenu.setBackground(Background.EMPTY);
            forMeLabel.setTextFill(Color.BLACK);
            forMeIcon.setImage(new Image(getClass().getResource("/image/user.png").toExternalForm()));
        }

        if (activeMyProject) {
            myProjectMenu.setBackground(new Background(new BackgroundFill(Color.web("#4896FE"), new CornerRadii(5), null)));
            myProjectLabel.setTextFill(Color.WHITE);
            myProjectIcon.setImage(new Image(getClass().getResource("/image/down-arrow.png").toExternalForm()));
            myProjectIcon.setRotate(0);
            addProjectBtn.getStyleClass().add("active-btn");
        } else {
            myProjectMenu.setBackground(Background.EMPTY);
            myProjectLabel.setTextFill(Color.BLACK);
            myProjectIcon.setImage(new Image(getClass().getResource("/image/right-arrow.png").toExternalForm()));
//            myProjectIcon.setRotate(90);
            addProjectBtn.getStyleClass().remove("active-btn");
        }
    }

    public void resetActive() {
        activeForMe = false;
        activeMyProject = false;
        updateStyles();
        projectListContainer.setVisible(false);
    }

    @FXML
    public void handleToggleProjectMenu(javafx.scene.input.MouseEvent mouseEvent) {
        activeMyProject = true;
        activeForMe = false;
        updateStyles();
        if (projectListContainer.isVisible()) {
            projectListContainer.setVisible(false);
        } else {
            projectListContainer.setVisible(true);
        }
    }
}