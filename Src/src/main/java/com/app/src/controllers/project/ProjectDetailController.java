package com.app.src.controllers.project;

import com.app.src.authentication.RoleValidator;
import com.app.src.authentication.VisibleManer;
import com.app.src.controllers.ViewNavigator;
import com.app.src.core.AppContext;
import com.app.src.models.Project;
import com.app.src.services.ProjectJoiningService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
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

    @FXML
    private Button tabSummary;

    @FXML
    private Button tabList;

    @FXML
    private Button tabBoard;

    @FXML
    private Button tabMember;

    @FXML
    private Button tabInfor;

    @FXML
    private Hyperlink breadcrumbHome;
    @FXML
    private Hyperlink breadcrumbProjects;
    @FXML
    private Label breadcrumbProjectName;

    private static final String TAB_ACTIVE_CLASS = "project-detail-tab-active";

    private Project project;
    private String adminName;
    private String userRoleName;
    private  final int userId= AppContext.getUserData().getUserId();

    public void renderData(Project project, String adminName) {

        this.project = project;
        this.adminName = adminName;

        lblProjectName.setText(project.getProjectName());
        if (breadcrumbProjectName != null) {
            breadcrumbProjectName.setText(project.getProjectName());
        }
        ProjectDetailNavigator.getInstance().setMainContentArea(tabContentArea);

        userRoleName = project.getUserRoleName();
        //Nếu là Member thì chỉ render List,  Member và Infor
        if (!RoleValidator.getInstance().isManagerOrAdmin(userRoleName)) {
            //ẩn summary và board
            VisibleManer.hideNode(tabSummary);
            VisibleManer.hideNode(tabBoard);

            //render List đầu
            ListController listController = ProjectDetailNavigator.getInstance().loadSubView("/components/ProjectDetail/List.fxml");
            listController.renderData(project, adminName);
            setActiveTab(tabList);

        } else{
            //render trang Summary dau tien
            SummaryController summaryController = ProjectDetailNavigator.getInstance().loadSubView("/components/ProjectDetail/Summary.fxml");
            summaryController.renderData(project, adminName);
            // Mặc định highlight tab Summary khi mở màn hình Project Detail.
            setActiveTab(tabSummary);
        }

    }


    public void handleNavbarClick(MouseEvent mouseEvent) {
        Button clickedNode = (Button) mouseEvent.getSource();
        String btnId = clickedNode.getId();
        System.out.println("Clicked on button " + btnId.substring(3));

        IProjectDetailSubView controller = ProjectDetailNavigator.getInstance().loadSubView("/components/ProjectDetail/"+btnId.substring(3) + ".fxml");
        controller.renderData(project, adminName);

        setActiveTab(clickedNode);
    }

    private void setActiveTab(Button selectedTab) {
        for (Node node : projectDetailNavBar.getChildren()) {
            if (node instanceof Button button) {
                button.getStyleClass().remove(TAB_ACTIVE_CLASS);
            }
        }

        if (!selectedTab.getStyleClass().contains(TAB_ACTIVE_CLASS)) {
            selectedTab.getStyleClass().add(TAB_ACTIVE_CLASS);
        }
    }

    public void handleBackClick(MouseEvent mouseEvent) {
        ViewNavigator.getInstance().loadSubScene("/scenes/ProjectList.fxml");
    }

    @FXML
    private void handleBreadcrumbClick(javafx.event.ActionEvent event) {
        Object source = event.getSource();
        if (source == breadcrumbHome) {
            ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
            return;
        }
        if (source == breadcrumbProjects) {
            ViewNavigator.getInstance().loadSubScene("/scenes/ProjectList.fxml");
        }
    }
}
