package com.app.src.controllers.project;

import com.app.src.controllers.ViewNavigator;
import com.app.src.core.AppContext;
import com.app.src.models.Project;
import com.app.src.services.ProjectJoiningService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class ProjectListController {

    @FXML
    private FlowPane allProjectsFlowPane;

    private final ProjectJoiningService projectJoiningService = new ProjectJoiningService();
    @FXML
    public void initialize() {
        renderProjectList();
    }

    private void setData(ArrayList<Project> projects) {
//        System.out.println("Set data");
        for (int index = 0; index < projects.size(); index++) {

            Project project = projects.get(index);

            String adminName = projectJoiningService.getAdmin(project.getProjectId());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/ProjectCard.fxml"));
                VBox card = loader.load();

                // set data for project card
                ProjectCardController projectCardController = loader.getController();
                projectCardController.setData(project.getProjectName(), adminName);

                // Gắn ID vào UserData để nhận diện khi click
                card.setUserData(index);

                allProjectsFlowPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void renderProjectList() {
//        System.out.println("Project List controller");
//        get all projects data and set for project card
        AppContext.getInstance();
        ArrayList<Project> projects = AppContext.getProjects();
//        System.out.println("project list" + projects.size());
        setData(projects);

        // Gắn 1 listener duy nhất cho container cha
        allProjectsFlowPane.setOnMouseClicked(event -> {
            // Kiểm tra xem đối tượng bị click có phải là (hoặc nằm trong) Project Card không
            Node clickedNode = (Node) event.getTarget();

            // Truy ngược lên để tìm VBox (Project Card)
            Node card = findProjectCard(clickedNode);

            if (card != null && card.getUserData() != null) {
                int index = (Integer) card.getUserData();
//                System.out.println("Chuyển trang cho Project ID: " + projectId);

               ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");

               Project project = projects.get(index);
               String adminName = projectJoiningService.getAdmin(project.getProjectId());
               controller.renderData(project, adminName);
            }
        });
    }

    // Hàm hỗ trợ tìm Project Card từ node bị click
    private Node findProjectCard(Node node) {
        while (node != null && node != allProjectsFlowPane) {
            if (node instanceof VBox && node.getUserData() != null) {
                return node;
            }
            node = node.getParent();
        }
        return null;
    }
}
