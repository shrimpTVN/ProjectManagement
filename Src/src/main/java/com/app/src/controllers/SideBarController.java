package com.app.src.controllers;

import com.app.src.controllers.project.ProjectDetailController;
import com.app.src.core.AppContext;
import com.app.src.daos.ProjectDAO;
import com.app.src.models.Project;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class SideBarController implements Initializable {

    private static SideBarController instance;

    @FXML
    private Button addProjectBtn;

    @FXML
    private VBox projectListContainer;

    @FXML
    private TextField searchProjectField;

    @FXML
    private VBox projectItemsBox;

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

    private final ProjectJoiningService projectJoiningService = new ProjectJoiningService();
    private ArrayList<Project> myProjects = new ArrayList<>();
    private int selectedProjectId = -1;

    private boolean activeForMe = false;
    private boolean activeMyProject = false;

    public static SideBarController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        addProjectBtn.setOnAction(event -> openCreateProject());
        addProjectBtn.setOnMouseClicked(event -> event.consume());

        forMeMenu.setOnMouseClicked(event -> {
            activeForMe = true;
            activeMyProject = false;
            updateStyles();
            setProjectListVisible(false);
            handleForMeClick();
        });

        searchProjectField.textProperty().addListener((observable, oldValue, newValue) -> renderProjectItems(newValue));

        loadMyProjects();

        activeForMe = true;
        updateStyles();
        setProjectListVisible(false);
    }

    public void reloadProjects() {
        AppContext.refreshProjects();
        loadMyProjects();
    }

    private void loadMyProjects() {
        int currentUserId = AppContext.getInstance().getUserData().getUserId();
        myProjects = ProjectService.getAdminProjects(currentUserId);
        if (myProjects == null) {
            myProjects = new ArrayList<>();
        }
        renderProjectItems(searchProjectField != null ? searchProjectField.getText() : "");
    }

    private void renderProjectItems(String keyword) {
        if (projectItemsBox == null) {
            return;
        }

        projectItemsBox.getChildren().clear();
        String normalizedKeyword = normalize(keyword);

        for (Project project : myProjects) {
            String projectName = project.getProjectName() == null ? "" : project.getProjectName();
            if (!normalize(projectName).contains(normalizedKeyword)) {
                continue;
            }

            boolean isManager = project.getUserRoleId() == 1;
            String displayName = isManager ? projectName + " (M)" : projectName;

            Label projectItem = new Label(displayName);
            projectItem.getStyleClass().add("project-item-label");
            projectItem.setWrapText(true);
            projectItem.setMaxWidth(Double.MAX_VALUE);
            if (project.getProjectId() == selectedProjectId) {
                projectItem.getStyleClass().add("project-item-active");
            }
            projectItem.setOnMouseClicked(event -> {
                selectedProjectId = project.getProjectId();
                renderProjectItems(searchProjectField.getText());
                openProjectDetail(project);
            });
            projectItemsBox.getChildren().add(projectItem);
        }

        if (projectItemsBox.getChildren().isEmpty()) {
            Label emptyLabel = new Label("Khong tim thay project");
            emptyLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #777777;");
            projectItemsBox.getChildren().add(emptyLabel);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private void openProjectDetail(Project project) {
        ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");
        if (controller == null) {
            return;
        }
        String adminName = projectJoiningService.getAdmin(project.getProjectId());
        controller.renderData(AppContext.getProjectById(project.getProjectId()), adminName);
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
            forMeMenu.setBackground(new Background(new BackgroundFill(Color.web("#4896FE"), new CornerRadii(5), null)));
            forMeLabel.setTextFill(Color.WHITE);
            forMeIcon.setImage(new Image(getClass().getResource("/image/user_white.png").toExternalForm()));
        } else {
            forMeMenu.setBackground(Background.EMPTY);
            forMeLabel.setTextFill(Color.BLACK);
            forMeIcon.setImage(new Image(getClass().getResource("/image/user.png").toExternalForm()));
        }

        if (activeMyProject) {
            myProjectMenu.setBackground(new Background(new BackgroundFill(Color.web("#4896FE"), new CornerRadii(5), null)));
            myProjectLabel.setTextFill(Color.WHITE);
            myProjectIcon.setImage(new Image(getClass().getResource("/image/down-arrow.png").toExternalForm()));
            myProjectIcon.setRotate(0);
            if (!addProjectBtn.getStyleClass().contains("active-btn")) {
                addProjectBtn.getStyleClass().add("active-btn");
            }
        } else {
            myProjectMenu.setBackground(Background.EMPTY);
            myProjectLabel.setTextFill(Color.BLACK);
            myProjectIcon.setImage(new Image(getClass().getResource("/image/right-arrow.png").toExternalForm()));
            addProjectBtn.getStyleClass().remove("active-btn");
        }
    }

    public void resetActive() {
        activeForMe = false;
        activeMyProject = false;
        updateStyles();
        setProjectListVisible(false);
    }

    private void setProjectListVisible(boolean visible) {
        projectListContainer.setVisible(visible);
        projectListContainer.setManaged(visible);
    }

    @FXML
    public void handleToggleProjectMenu(javafx.scene.input.MouseEvent mouseEvent) {
        activeMyProject = true;
        activeForMe = false;
        updateStyles();

        boolean nextVisible = !projectListContainer.isVisible();
        setProjectListVisible(nextVisible);

        if (nextVisible) {
            renderProjectItems(searchProjectField.getText());
        }
    }
}
