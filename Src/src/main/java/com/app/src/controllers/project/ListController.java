package com.app.src.controllers.project;

import com.app.src.authentication.RoleValidator;
import com.app.src.authentication.VisibleManer;
import com.app.src.controllers.ViewNavigator;
import com.app.src.controllers.task.CreateTaskController;
import com.app.src.controllers.task.TaskDetailController;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Project;
import com.app.src.models.Task;
import com.app.src.services.TasklistService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

public class ListController implements IProjectDetailSubView, Initializable {

    // ==========================================
    // KHAI BÁO THÀNH PHẦN GIAO DIỆN (UI)
    // ==========================================
    @FXML
    private TableView<Task> taskTable;
    @FXML
    private TableColumn<Task, String> colName, colAssignee, colStart, colDeadline, colStatus, colDescription;
    @FXML
    private Hyperlink hlAll, hlInPreview;

    // ==========================================
    // KHAI BÁO DỮ LIỆU & SERVICE
    // ==========================================
    private final TasklistService service = new TasklistService();
    private final ObservableList<Task> masterData = FXCollections.observableArrayList();
    private Project project;

    @FXML
    private Button btnCreate;
    @FXML
    private Button btnDelete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupLinkActions();


    }

    @Override
    public void renderData(Project project, String adminName) {
        this.project = project;
        if (project != null) {
            loadData(project.getProjectId());
        }

        if (!RoleValidator.isManagerOrAdmin(project.getUserRoleName())) {
            VisibleManer.hideNode(btnCreate);
            VisibleManer.hideNode(btnDelete);
        } else {
            btnCreate.setOnAction(event -> {
                CreateTaskController controller = ViewNavigator.getInstance().loadSubScene("/scenes/CreateTask.fxml");
                if (controller != null) {
                    controller.setProject(this.project);
                }
            });
            btnDelete.disableProperty().bind(taskTable.getSelectionModel().selectedItemProperty().isNull());
            btnDelete.setOnAction(event -> handleDeleteTask());
        }

    }

    private void setupTableColumns() {
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskName()));

        colName.setCellFactory(column -> {
            return new TableCell<Task, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        setStyle("-fx-cursor: hand;");

                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                Task clickedTask = getTableView().getItems().get(getIndex());
                                handleOpenTaskDetail(clickedTask);
                            }
                        });
                    }
                }
            };
        });

        colAssignee.setCellValueFactory(cd -> {
            if (cd.getValue().getUser() != null) return new SimpleStringProperty(cd.getValue().getUser().getUserName());
            return new SimpleStringProperty("N/A");
        });
        colStart.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskStartTime()));
        colDeadline.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskEndTime()));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(formatStatusForDisplay(cd.getValue().getTaskStatus())));
        colStatus.setCellFactory(col -> new StatusColorCell());
        colDescription.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskDescription()));
    }

    // Custom TableCell để hiển thị status với badge colors
    private class StatusColorCell extends TableCell<Task, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            Task task = getTableView().getItems().get(getIndex());

            // Kiểm tra quá deadline
            String bgColor;
            String textColor;
            if (isTaskOverdue(task)) {
                bgColor = "#FECACA";
                textColor = "#991B1B";
            } else {
                String normalizedStatus = normalizeStatus(task.getTaskStatus());
                switch (normalizedStatus) {
                    case "to do":
                        bgColor = "#FEF08A";
                        textColor = "#854D0E";
                        break;
                    case "in progress":
                        bgColor = "#DBEAFE";
                        textColor = "#1D4ED8";
                        break;
                    case "in preview":
                        bgColor = "#E9D5FF";
                        textColor = "#6B21A8";
                        break;
                    case "done":
                        bgColor = "#BBF7D0";
                        textColor = "#166534";
                        break;
                    default:
                        bgColor = "transparent";
                        textColor = "#000000";
                }
            }

            // Tạo label badge để background chỉ bao quanh text
            Label badge = new Label(item);
            badge.setStyle(String.format(
                    "-fx-padding: 4 10 4 10; -fx-background-color: %s; -fx-text-fill: %s; -fx-background-radius: 6; -fx-border-radius: 6; -fx-font-size: 13px; -fx-font-weight: 600;",
                    bgColor, textColor));

            setGraphic(badge);
            setText(null);
            setAlignment(Pos.CENTER);
        }
    }

    private boolean isTaskOverdue(Task task) {
        if (task == null || task.getTaskEndTime() == null) {
            return false;
        }

        String deadlineStr = task.getTaskEndTime().trim();
        if (deadlineStr.length() >= 10) {
            deadlineStr = deadlineStr.substring(0, 10);
        }

        try {
            LocalDate deadline = LocalDate.parse(deadlineStr);
            String normalizedStatus = normalizeStatus(task.getTaskStatus());

            // Chỉ xem là quá deadline nếu status không phải "done" và deadline < today
            return !normalizedStatus.equals("done") && deadline.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String normalizeStatus(String rawStatus) {
        if (rawStatus == null) {
            return "";
        }

        String normalized = rawStatus.trim().toLowerCase();
        if (normalized.equals("todo") || normalized.equals("to do")) {
            return "to do";
        }
        if (normalized.equals("in processing") || normalized.equals("in progressing") || normalized.equals("in progress")) {
            return "in progress";
        }
        if (normalized.equals("in preview")) {
            return "in preview";
        }
        if (normalized.equals("done")) {
            return "done";
        }
        return normalized;
    }

    // Chuẩn hoá text status để UI hiển thị nhất quán.
    private String formatStatusForDisplay(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return "To Do";
        }

        String normalized = rawStatus.trim().toLowerCase();
        if (normalized.equals("in progressing") || normalized.equals("progressing")) {
            return "In Progress";
        }
        if (normalized.equals("in preview")) {
            return "In Preview";
        }
        if (normalized.equals("done")) {
            return "Done";
        }
        if (normalized.equals("to do") || normalized.equals("todo")) {
            return "To Do";
        }

        return rawStatus;
    }

    private void setupLinkActions() {
        setActiveLink(hlAll);

        hlAll.setOnAction(e -> {
            setActiveLink(hlAll);
            taskTable.setItems(masterData);
        });

        hlInPreview.setOnAction(e -> {
            setActiveLink(hlInPreview);
            taskTable.setItems(masterData.filtered(task -> "In Preview".equals(formatStatusForDisplay(task.getTaskStatus()))));
        });
    }

    private void setActiveLink(Hyperlink selected) {
        hlAll.getStyleClass().remove("filter-item-active");
        hlInPreview.getStyleClass().remove("filter-item-active");

        selected.getStyleClass().add("filter-item-active");
    }

    public void loadData(int projectId) {
        masterData.setAll(service.getTasksByProject(projectId, project.getUserRoleName()));
        applyCurrentFilter();
    }

    private void applyCurrentFilter() {
        if (hlInPreview.getStyleClass().contains("filter-item-active")) {
            taskTable.setItems(masterData.filtered(task -> "In Preview".equals(formatStatusForDisplay(task.getTaskStatus()))));
        } else {
            taskTable.setItems(masterData);
        }
    }

    private void handleOpenTaskDetail(Task task) {
        try {
            Object controller = ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");

            if (controller instanceof TaskDetailController detailController) {
                detailController.setProjectId(project.getProjectId());

                PersonalTaskDTO dto = new PersonalTaskDTO();
                dto.setTaskId(task.getTaskId());
                dto.setTaskName(task.getTaskName());
                dto.setTaskDescription(task.getTaskDescription());
                dto.setTaskStartTime(task.getTaskStartTime());
                dto.setTaskEndTime(task.getTaskEndTime());
                dto.setStatusName(formatStatusForDisplay(task.getTaskStatus()));
                if (project != null) {
                    dto.setProjectName(project.getProjectName());
                }

                detailController.setTaskData(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteTask() {
        Task selectedTask = taskTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete task: " + selectedTask.getTaskName());
        alert.setContentText("Are you sure you want to delete this task?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            TasklistService service = new TasklistService();
            boolean success = service.deleteTask(selectedTask.getTaskId());

            if (success) {
                System.out.println("Task deleted successfully!");
                loadData(project.getProjectId());
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setContentText("System error: Unable to delete task.");
                errorAlert.show();
            }
        }
    }
}
