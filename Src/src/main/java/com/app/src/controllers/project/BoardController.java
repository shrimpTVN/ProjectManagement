package com.app.src.controllers.project;

import com.app.src.models.Project;
import com.app.src.dtos.PersonalTaskDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label; // Quan trọng: Import Label của JavaFX

import com.app.src.services.TasklistService;

// Xóa dòng import java.awt.*; đi nhé
import java.io.IOException;
import java.util.List;

public class BoardController implements IProjectDetailSubView {

    @FXML private VBox toDoList;
    @FXML private VBox inProgressList;
    @FXML private VBox inPreviewList;
    @FXML private VBox doneList;

    @FXML private Label toDoCount;
    @FXML private Label inProgressCount;
    @FXML private Label inPreviewCount;
    @FXML private Label doneCount;

    @Override
    public void renderData(Project project, String adminName) {
        System.out.println("Loading Board for: " + project.getProjectName());

        // 1. Xóa dữ liệu cũ
        toDoList.getChildren().clear();
        inProgressList.getChildren().clear();
        inPreviewList.getChildren().clear();
        doneList.getChildren().clear();

        // Đặt lại các bộ đếm về 0 khi xóa dữ liệu
        updateCounts();

        if (project == null) return;

        // 2. Khởi tạo Service và lấy danh sách Task
        TasklistService taskService = new TasklistService();
        List<PersonalTaskDTO> tasks = taskService.getTasksByProjectId(project.getProjectId());

        if (tasks == null || tasks.isEmpty()) {
            System.out.println("Dự án này chưa có task nào.");
            return;
        }

        // 3. Duyệt qua từng Task và nạp vào cột
        for (PersonalTaskDTO task : tasks) {
            String taskName = task.getTaskName();
            String status = task.getStatusName();

            String assignee = (task.getUser() != null && task.getUser().getUserName() != null)
                    ? task.getUser().getUserName()
                    : "Chưa phân công";

            if (status == null) continue;

            switch (status) {
                case "To Do":
                    addTaskToColumn(toDoList, taskName, assignee);
                    break;
                case "In Progress":
                case "In Progressing":
                    addTaskToColumn(inProgressList, taskName, assignee);
                    break;
                case "In Preview":
                    addTaskToColumn(inPreviewList, taskName, assignee);
                    break;
                case "Done":
                    addTaskToColumn(doneList, taskName, assignee);
                    break;
                default:
                    break;
            }
        }

        // 4. SAU KHI VÒNG LẶP KẾT THÚC -> CẬP NHẬT LẠI SỐ LƯỢNG (COUNT)
        updateCounts();
    }

    // Hàm riêng để cập nhật các Label đếm số lượng
    private void updateCounts() {
        if (toDoCount != null) toDoCount.setText(String.valueOf(toDoList.getChildren().size()));
        if (inProgressCount != null) inProgressCount.setText(String.valueOf(inProgressList.getChildren().size()));
        if (inPreviewCount != null) inPreviewCount.setText(String.valueOf(inPreviewList.getChildren().size()));
        if (doneCount != null) doneCount.setText(String.valueOf(doneList.getChildren().size()));
    }

    private void addTaskToColumn(VBox columnContainer, String taskName, String assignee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/ProjectDetail/TaskCard.fxml"));
            Node taskCard = loader.load();

            TaskCardController controller = loader.getController();
            controller.setData(taskName, assignee);

            columnContainer.getChildren().add(taskCard);

        } catch (IOException e) {
            System.err.println("Lỗi nạp TaskCard: " + e.getMessage());
        }
    }
}