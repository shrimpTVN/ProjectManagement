package com.app.src.controllers.project;

import com.app.src.controllers.CreateProjectController;
import com.app.src.controllers.ViewNavigator;
import com.app.src.core.AppContext;
import com.app.src.models.Project;
import com.app.src.models.ProjectJoining;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;

public class InforController implements IProjectDetailSubView, Initializable {

    @FXML
    private Label lblDescription;
    @FXML
    private Label lblStartDate;
    @FXML
    private Label lblEndDate;
    @FXML
    private Label lblMemberCount;
    @FXML
    private Label lblAdminName;
    @FXML
    private Label lblManagerName;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;

    private Project currentProject;
    private ProjectService projectService = new ProjectService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnEdit.setOnAction(e -> handleEdit());
        btnDelete.setOnAction(e -> handleDelete());
    }

    private void handleEdit() {
        // 1. Load màn hình và lấy ngay Controller của nó
        CreateProjectController controller = ViewNavigator.getInstance().loadSubScene("/scenes/CreateProject.fxml");

        // 2. Nếu load thành công, "nhồi" dữ liệu vào
        if (controller != null) {
            controller.setProjectInfo(currentProject);
        }
    }

    private void handleDelete() {
        System.out.println("ID chuẩn bị xóa là: " + currentProject.getProjectId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa dự án này?");
        alert.setContentText("Dữ liệu sau khi xóa sẽ KHÔNG thể khôi phục.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean isDeleted = projectService.deleteProject(currentProject.getProjectId());

            if (isDeleted) {
                AppContext.refreshProjects();
                ViewNavigator.getInstance().loadSubScene("/scenes/ProjectList.fxml");
            } else {
                System.out.println("Lỗi: Xóa dự án thất bại.");
            }
        }
    }

    @Override
    public void renderData(Project project, String adminName) {
        this.currentProject = project;
        if (project == null) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        lblDescription.setText(project.getProjectDescription());
        lblStartDate.setText(project.getProjectStartDate() != null ? dateFormat.format(project.getProjectStartDate()) : "");
        lblEndDate.setText(project.getProjectEndDate() != null ? dateFormat.format(project.getProjectEndDate()) : "");
        lblAdminName.setText(adminName);

        // Đếm số lượng thành viên và tìm Manager
        int memberCount = 0;
        String managerName = adminName;       // Mặc định là admin, nếu không tìm thấy Manager nào khác thì vẫn hiển thị admin
        ProjectJoiningService joiningService = new ProjectJoiningService();
        project.setJoinings(joiningService.findAllJoiningsByProjectId(project.getProjectId()));
        if (project.getJoinings() != null) {
            memberCount = project.getJoinings().size();
            // Duyệt danh sách để tìm người có Role là Project Manager (Role_id = 1)
            for (ProjectJoining joining : project.getJoinings()) {
                // Kiểm tra Role ID = 1 (Project Manager) hoặc tên role là "Project Manager"
                if (joining.getRole() != null &&
                        (joining.getRole().getRoleId() == 1 ||
                                "Project Manager".equalsIgnoreCase(joining.getRole().getRoleName()))) {
                    managerName = joining.getUser().getUserName();
                    break;
                }
            }
        }
        lblMemberCount.setText(String.valueOf(memberCount));
        lblManagerName.setText(managerName);
    }
}