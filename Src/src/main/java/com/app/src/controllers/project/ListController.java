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

import java.net.URL;
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
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskStatus()));
        colDescription.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskDescription()));
    }

    private void setupLinkActions() {
        setActiveLink(hlAll);

        hlAll.setOnAction(e -> {
            setActiveLink(hlAll);
            taskTable.setItems(masterData);
        });

        hlInPreview.setOnAction(e -> {
            setActiveLink(hlInPreview);
            taskTable.setItems(masterData);
        });
    }

    private void setActiveLink(Hyperlink selected) {
        hlAll.getStyleClass().remove("filter-item-active");
        hlInPreview.getStyleClass().remove("filter-item-active");

        selected.getStyleClass().add("filter-item-active");
    }

    public void loadData(int projectId) {
        masterData.setAll(service.getTasksByProject(projectId, project.getUserRoleName()));
        taskTable.setItems(masterData);
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
                dto.setStatusName(task.getTaskStatus());
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