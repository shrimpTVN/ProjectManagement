package com.app.src.controllers.project;

import com.app.src.models.Project;
import com.app.src.models.ProjectJoining;
import com.app.src.services.ProjectJoiningService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.text.SimpleDateFormat;

public class InforController implements IProjectDetailSubView {

    @FXML private Label lblDescription;
    @FXML private Label lblStartDate;
    @FXML private Label lblEndDate;
    @FXML private Label lblMemberCount;
    @FXML private Label lblAdminName;
    @FXML private Label lblManagerName;

    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    @Override
    public void renderData(Project project, String adminName) {
        if (project == null) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        lblDescription.setText(project.getProjectDescription());
        lblStartDate.setText(project.getProjectStartDate() != null ? dateFormat.format(project.getProjectStartDate()) : "");
        lblEndDate.setText(project.getProjectEndDate() != null ? dateFormat.format(project.getProjectEndDate()) : "");
        lblAdminName.setText(adminName);

        // Đếm số lượng thành viên và tìm Manager
        int memberCount = 0;
        String managerName = "Chưa phân công";
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