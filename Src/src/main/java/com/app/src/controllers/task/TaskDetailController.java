//package com.app.src.controllers;
//

//public class TaskDetailController {
//    private PersonalTaskDTO currentTask;
//
//    // Viết 1 hàm public để nhận dữ liệu truyền tới
//    public void setTaskData(PersonalTaskDTO task) {
//        this.currentTask = task;
//
//        // Nhận được data xong thì cập nhật lên giao diện luôn
//        System.out.println("Đã nhận được task: " + task.getTaskName());
//        // txtName.setText(task.getTaskName());
//    }
//
//}
//

package com.app.src.controllers.task;

import com.app.src.authentication.RoleValidator;
import com.app.src.controllers.ViewNavigator;
import com.app.src.controllers.project.ProjectDetailController;
import com.app.src.core.AppContext;
import com.app.src.core.service.chat.ChatClientService;
import com.app.src.daos.ProjectDAO;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Project;
import com.app.src.models.Task;
import com.app.src.services.ProjectJoiningService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import com.app.src.ui.components.TaskButton.*;

import java.io.IOException;

public class TaskDetailController {

    private static final String ACTIVE_TAB_STYLE_CLASS = "active-tab";
    // --- Các thành phần giao diện đã được khai báo trong file FXML ---
    @FXML
    private Label lblProjectName;
    // Labels hiển thị thông tin chính
    @FXML
    private Label lblTask;
    @FXML
    private Label lblTaskName;
    @FXML
    private Label lblNameProject;
    @FXML
    private Label lblTaskStatus;
    @FXML
    private Label lblStart;
    @FXML
    private Label lblDeadline;
    @FXML
    private Label lblReporter; // (Hiện tại DTO chưa có trường này, tạm thời để trống hoặc lấy từ Session)
    @FXML
    private Label lblAuthor;   // (Tương tự lblReporter)
    @FXML
    private Label lblDescription;

    @FXML
    private Hyperlink breadcrumbHome;
    @FXML
    private Hyperlink breadcrumbAllTasks;
    @FXML
    private Label breadcrumbTaskName;

    // Các nút bấm
    @FXML
    private Button btnCmt;
    @FXML
    private Button btnHistory;
    @FXML
    private Button btnEdit; // Nút chỉnh sửa
    @FXML

    private ScrollPane taskDetailSubViewContainer;
    // Biến lưu trữ Task hiện tại đang xem
    private PersonalTaskDTO currentTask;
    private int fromProject = 0;
    private String currentSubView;

    /**
     * Hàm này được gọi từ TasklistController để truyền dữ liệu Task vào
     */
    public void setTaskData(PersonalTaskDTO task) {
        if (task == null) {
            System.err.println("Lỗi: Không nhận được dữ liệu Task!");
            return;
        }

        this.currentTask = task;

        // Đổ dữ liệu từ Object Task lên các Label trên màn hình
        String safeTaskName = task.getTaskName() != null ? task.getTaskName() : "(Không có tên task)";
        String safeProjectName = task.getProjectName() != null ? task.getProjectName() : "Chưa gắn dự án";
        String safeStatus = task.getStatusName() != null ? task.getStatusName() : "Trạng thái trống";

        lblTaskName.setText(safeTaskName); // Cập nhật tên Task (cho tiêu đề lớn)
        lblNameProject.setText(safeProjectName);
        lblProjectName.setText("Project: " + safeProjectName);
        lblTaskStatus.setText(safeStatus);

        if (breadcrumbTaskName != null) {
            breadcrumbTaskName.setText(safeTaskName);
        }

        lblStart.setText(task.getTaskStartTime() != null ? task.getTaskStartTime() : "Chưa xác định");
        lblDeadline.setText(task.getTaskEndTime() != null ? task.getTaskEndTime() : "Chưa xác định");

        lblDescription.setText(task.getTaskDescription() != null ? task.getTaskDescription() : "Không có mô tả.");

        // Cập nhật nút Dropdown Trạng thái
//        menubtnStatus.setText(task.getStatusName() != null ? task.getStatusName() : "Trạng thái");

        renderActionButtons(task);
        updateEditButtonVisibility();

        // --- Phần này bạn cần bổ sung thêm nếu muốn hiển thị ---
        // lblReporter.setText("Người báo cáo...");
        // lblAuthor.setText(task.getUser().getUserName()); // Nếu Model của bạn hỗ trợ lấy người tạo

        // LOGIC TỰ ĐỘNG THU NHỎ FONT CHO TASK NAME
        String taskName = safeTaskName;
        if (taskName.length() > 50) {
            lblTaskName.setStyle("-fx-font-size: 20px;"); // Rất dài -> font nhỏ
        } else if (taskName.length() > 25) {
            lblTaskName.setStyle("-fx-font-size: 28px;"); // Vừa phải -> font vừa
        } else {
            lblTaskName.setStyle("-fx-font-size: 36px;"); // Ngắn -> font to (mặc định)
        }

        this.currentSubView = "CommentBox";
        loadTaskDetailSubView(currentSubView);
        applySubViewButtonStyle();
    }

    public void setProjectId(int id) {
        this.fromProject = id;
        updateEditButtonVisibility();
    }

    private void updateEditButtonVisibility() {
        if (btnEdit == null) {
            return;
        }

        // Mặc định ẩn nút Edit cho mọi trường hợp không xác định rõ role.
        btnEdit.setVisible(false);
        btnEdit.setManaged(false);

        if (fromProject <= 0) {
            return;
        }

        for (Project project : AppContext.getProjects()) {
            if (project.getProjectId() == fromProject) {
                String roleName = project.getUserRoleName();
                boolean canEdit = roleName != null && RoleValidator.isManagerOrAdmin(roleName);
                btnEdit.setVisible(canEdit);
                btnEdit.setManaged(canEdit);
                return;
            }
        }
    }

    public void handleBackClick(MouseEvent mouseEvent) {
        if (this.fromProject != 0) {
            ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");

            // Lấy Project đầy đủ với PROJECT_JOINING data
            Project fullProject = AppContext.getProjectById(this.fromProject);
            String adminName = ProjectJoiningService.getAdmin(fromProject);
            controller.renderData(fullProject, adminName);

        } else {
            ViewNavigator.getInstance().loadSubScene("/scenes/tasklist.fxml");
        }
    }

    @FXML
    private void handleBreadcrumbClick(javafx.event.ActionEvent event) {
        Object source = event.getSource();
        if (source == breadcrumbHome) {
            ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
            return;
        }
        if (source == breadcrumbAllTasks) {
            ViewNavigator.getInstance().loadSubScene("/scenes/tasklist.fxml");
        }
    }

    public void loadTaskDetailSubView(String componentName) {
        System.out.println("Loading task detail subview for: " + componentName);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/TaskDetail/" + componentName + ".fxml"));
        try {
            if (currentTask == null) {
                throw new IllegalStateException("Task data is not initialized before loading subview");
            }
            Node taskDetailSubView = loader.load();
            taskDetailSubViewContainer.setContent(taskDetailSubView);

            Object childController = loader.getController();
            if (childController instanceof CommentBoxController commentController) {
                commentController.renderData(currentTask.getTaskId());
                ChatClientService.getInstance().setMessageListener(commentController);
                commentController.sendComment("req", AppContext.getUserData().getUserId(), "request to connecto to chat box");

            } else if (childController instanceof StatusNotiController statusNotiController) {  //gọi renderData từ container chứa status
                statusNotiController.renderData(currentTask.getTaskId());
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleSwitchSubViewClick(MouseEvent mouseEvent) {
        Object target = mouseEvent.getSource();

        if (target instanceof Button clickedButton) {
            String buttonText = clickedButton.getText();

            if (buttonText.equals("Cmt") & !currentSubView.equals("Comment")) {

                currentSubView = "CommentBox";
                loadTaskDetailSubView(currentSubView);
                applySubViewButtonStyle();
            } else if (!currentSubView.equals("StatusNotiContainer") & buttonText.equals("Status")) {
                //StatusNotiContainer là Vbox bao các status -> load vbox lên
                currentSubView = "StatusNotiContainer";
                loadTaskDetailSubView(currentSubView);

                applySubViewButtonStyle();
            }
        }
    }

    private void applySubViewButtonStyle() {
        boolean isCommentActive = "CommentBox".equals(currentSubView);
        boolean isStatusActive = "StatusNotiContainer".equals(currentSubView);

        toggleActiveTabStyle(btnCmt, isCommentActive);
        toggleActiveTabStyle(btnHistory, isStatusActive);
    }

    private void toggleActiveTabStyle(Button button, boolean active) {
        if (button == null) {
            return;
        }

        if (active) {
            if (!button.getStyleClass().contains(ACTIVE_TAB_STYLE_CLASS)) {
                button.getStyleClass().add(ACTIVE_TAB_STYLE_CLASS);
            }
            return;
        }

        button.getStyleClass().remove(ACTIVE_TAB_STYLE_CLASS);
    }

    @FXML
    private void handleEditClick(ActionEvent event) {
        if (currentTask == null) {
            return;
        }

        CreateTaskController controller = ViewNavigator.getInstance().loadSubScene("/scenes/CreateTask.fxml");
        if (controller != null) {
            controller.setEditDeadlineContext(currentTask, fromProject);
        }
    }
    @FXML
    private HBox hboxActionButtons; // Container mới thay cho MenuButton;

    private void renderActionButtons(PersonalTaskDTO taskDto) {
        if (hboxActionButtons == null) return;
        hboxActionButtons.getChildren().clear();

        String status = taskDto.getStatusName();
        if (status == null) return;

        Task taskModel = new Task();
        taskModel.setTaskId(taskDto.getTaskId());
        taskModel.setTaskName(taskDto.getTaskName());
        // Lấy User từ AppContext để truyền vào DB
        if (AppContext.getUserData() != null) {
            taskModel.setUser(AppContext.getUserData());
        }

        // --- KIỂM TRA ROLE CỦA USER TRONG PROJECT HIỆN TẠI ---
        boolean isManagerOrAdmin = false;
        if (fromProject > 0) {
            for (Project project : AppContext.getProjects()) {
                if (project.getProjectId() == fromProject) {
                    String roleName = project.getUserRoleName();
                    isManagerOrAdmin = roleName != null && RoleValidator.isManagerOrAdmin(roleName);
                    break;
                }
            }
        }

        System.out.println("- Trạng thái: " + status + " | Is Manager/Admin: " + isManagerOrAdmin);

        switch (status) {
            case "To Do":
                // Chỉ hiện cho Member (hoặc người được giao)
                if (!isManagerOrAdmin) {
                    AcceptTaskButton acceptBtn = new AcceptTaskButton();
                    acceptBtn.setup(taskModel, () -> {
                        System.out.println("Nhận task thành công!");
                        taskDto.setStatusName("In Progress");
                        setTaskData(taskDto);
                    });

                    RejectTaskButton rejectBtn = new RejectTaskButton();
                    rejectBtn.setup(taskModel, () -> {
                        System.out.println("Từ chối task thành công!");
                        taskDto.setStatusName("Canceled");
                        setTaskData(taskDto);
                    });

                    hboxActionButtons.getChildren().addAll(acceptBtn, rejectBtn);
                }
                break;

            case "In Progress":
            case "In Progressing":
                // Chỉ hiện cho Member
                if (!isManagerOrAdmin) {
                    SubmitReviewTaskButton submitBtn = new SubmitReviewTaskButton();
                    submitBtn.setup(taskModel, () -> {
                        System.out.println("Gửi duyệt thành công!");
                        taskDto.setStatusName("In Preview");
                        setTaskData(taskDto);
                    });
                    hboxActionButtons.getChildren().add(submitBtn);
                }
                break;

            case "In Preview":
                // CHỈ HIỆN CHO MANAGER HOẶC ADMIN
                if (isManagerOrAdmin) {
                    ApproveTaskButton approveBtn = new ApproveTaskButton();
                    approveBtn.setup(taskModel, () -> {
                        System.out.println("Duyệt task thành công!");
                        taskDto.setStatusName("Done");
                        setTaskData(taskDto);
                    });

                    RejectPreviewTaskButton rejectPreviewBtn = new RejectPreviewTaskButton();
                    rejectPreviewBtn.setup(taskModel, () -> {
                        System.out.println("Yêu cầu làm lại thành công!");
                        taskDto.setStatusName("In Progress"); // Trả về cho member làm lại
                        setTaskData(taskDto);
                    });

                    hboxActionButtons.getChildren().addAll(approveBtn, rejectPreviewBtn);
                }
                break;

            default:
                break;
        }
    }

}