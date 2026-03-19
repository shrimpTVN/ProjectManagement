package com.app.src.controllers.project;

import com.app.src.controllers.ViewNavigator;
import com.app.src.core.AppContext;
import com.app.src.daos.ProjectDAO;
import com.app.src.models.Project;
import com.app.src.services.ProjectJoiningService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ProjectListController {

    @FXML
    private FlowPane allProjectsFlowPane;

    @FXML
    private TextField searchProjectField;

    private final ProjectJoiningService projectJoiningService = new ProjectJoiningService();
    private final ArrayList<Project> allProjects = new ArrayList<>();

    @FXML
    public void initialize() {
        loadProjects();
        renderProjects(allProjects);
        setupSearch();
        setupCardClickHandler();
    }

    private void loadProjects() {
        AppContext.getInstance();
        allProjects.clear();
        allProjects.addAll(AppContext.getProjects());
    }

    private void renderProjects(ArrayList<Project> projects) {
        allProjectsFlowPane.getChildren().clear();

        for (Project project : projects) {
            String adminName = projectJoiningService.getAdmin(project.getProjectId());
            String roleName = projectJoiningService.getRoleInProject(AppContext.getUserData().getUserId(), project.getProjectId());
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/ProjectCard.fxml"));
                VBox card = loader.load();

                ProjectCardController projectCardController = loader.getController();
                projectCardController.setData(project.getProjectName(), roleName, adminName);

                // Gắn thẳng object Project để click vẫn đúng ngay cả khi danh sách đã lọc.
                card.setUserData(project);

                allProjectsFlowPane.getChildren().add(card);
            } catch (IOException e) {
                throw new RuntimeException("Khong the tai ProjectCard.fxml", e);
            }
        }
    }

    private void setupSearch() {
        if (searchProjectField == null) {
            return;
        }

        searchProjectField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.trim().toLowerCase(Locale.ROOT);
            ArrayList<Project> filteredProjects = new ArrayList<>();

            for (Project project : allProjects) {
                String projectName = project.getProjectName() == null ? "" : project.getProjectName().toLowerCase(Locale.ROOT);
                if (keyword.isEmpty() || projectName.contains(keyword)) {
                    filteredProjects.add(project);
                }
            }

            renderProjects(filteredProjects);
        });
    }

    private void setupCardClickHandler() {
        allProjectsFlowPane.setOnMouseClicked(event -> {
            Node clickedNode = (Node) event.getTarget();
            Node card = findProjectCard(clickedNode);

            if (card != null && card.getUserData() instanceof Project project) {
                int projectId = project.getProjectId();

                ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");

                String adminName = projectJoiningService.getAdmin(projectId);
                System.out.println(project.getUserRoleName());
                controller.renderData(project, adminName);
            }
        });
    }



    // Hàm hỗ trợ tìm Project Card từ node bị click
    private Node findProjectCard(Node node) {
        while (node != null && node != allProjectsFlowPane) {
            if (node instanceof VBox && node.getUserData() instanceof Project) {
                return node;
            }
            node = node.getParent();
        }
        return null;
    }
}
