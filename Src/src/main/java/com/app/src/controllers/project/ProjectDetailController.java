package com.app.src.controllers.project;

import com.app.src.models.Project;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


public class ProjectDetailController {

    @FXML
    StackPane tabContentArea;

    @FXML
    Label lblProjectName;

    @FXML
    HBox projectDetailNavBar;

    private Project project;
    private String adminName;

    public void renderData(Project project, String adminName) {

        this.project = project;
        this.adminName = adminName;

        lblProjectName.setText(project.getProjectName());
        ProjectDetailNavigator.getInstance().setMainContentArea(tabContentArea);
        SummaryController summaryController = ProjectDetailNavigator.getInstance().loadSubView("/components/ProjectDetail/Summary.fxml");
        summaryController.renderData(project, adminName);
    }


    public void handleNavbarClick(MouseEvent mouseEvent) {
        Button clickedNode = (Button) mouseEvent.getSource();
        String btnId = clickedNode.getId();
        System.out.println("Clicked on button " + btnId.substring(3));

        IProjectDetailSubView controller = ProjectDetailNavigator.getInstance().loadSubView("/components/ProjectDetail/"+btnId.substring(3) + ".fxml");
        controller.renderData(project, adminName);
    }
}
