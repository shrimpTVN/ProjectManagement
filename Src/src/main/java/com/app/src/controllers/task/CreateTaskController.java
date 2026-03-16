package com.app.src.controllers.task;

import com.app.src.controllers.ViewNavigator;
import com.app.src.controllers.project.ProjectDetailController;
import com.app.src.daos.ProjectDAO;
import com.app.src.daos.TaskDAO;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Project;
import com.app.src.models.Task;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.TasklistService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.format.DateTimeFormatter;

public class CreateTaskController {
    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private Label lblErrorName;

    private Project currentProject;
    private final TaskDAO taskDAO = TaskDAO.getInstance(); // Giả định dùng Singleton

    /**
     * Nhận project hiện tại để biết task này thuộc về đâu
     */
    public void setProject(Project project) {
        this.currentProject = project;
    }

    @FXML
    private void handleSaveTask() {
        PersonalTaskDTO dto = new PersonalTaskDTO();

        dto.setTaskName(txtName.getText().trim());
        dto.setTaskDescription(txtDescription.getText());

        // Gán ngày tháng
        if (dpStart.getValue() != null) dto.setTaskStartTime(dpStart.getValue().toString());
        if (dpEnd.getValue() != null) dto.setTaskEndTime(dpEnd.getValue().toString());

        // Quan trọng: Gán ID project để lưu vào đúng chỗ
        if (currentProject != null) {
            dto.setProjectId(currentProject.getProjectId());
        }

        try {
            TasklistService service = new TasklistService();
            if (service.addTask(dto)) { // Hàm này sẽ gọi DAO xử lý cả Task và TaskStatus ngầm bên dưới
                backToProjectDetail();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        backToProjectDetail();
    }

    private void backToProjectDetail() {
        if (currentProject == null) return;

        // Tải lại giao diện Project Detail thông qua Navigator
        ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");

        // Lấy lại dữ liệu mới nhất của project để render
        Project fullProject = ProjectDAO.getInstance().getProjectWithJoinings(currentProject.getProjectId());
        ProjectJoiningService joiningService = new ProjectJoiningService();
        String adminName = joiningService.getAdmin(currentProject.getProjectId());

        controller.renderData(fullProject, adminName);
    }
}