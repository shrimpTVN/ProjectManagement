package com.app.src.controllers.project;

import com.app.src.controllers.task.CreateTaskController;
import com.app.src.controllers.task.TaskDetailController;
import com.app.src.controllers.ViewNavigator;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Project;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
//import com.app.src.models.Task;
import com.app.src.services.TasklistService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class ListController implements IProjectDetailSubView, Initializable {


    // ==========================================
    // KHAI BÁO THÀNH PHẦN GIAO DIỆN (UI)
    // ==========================================
    @FXML
    private TableView<PersonalTaskDTO> taskTable;
    @FXML
    private TableColumn<PersonalTaskDTO, String> colName, colAssignee, colStart, colDeadline, colStatus, colDescription;
    @FXML
    private Hyperlink hlAll, hlInPreview;

    // ==========================================
    // KHAI BÁO DỮ LIỆU & SERVICE
    // ==========================================
    private TasklistService service = new TasklistService();
    private ObservableList<PersonalTaskDTO> masterData = FXCollections.observableArrayList();
    private Project project;

    /**
     * Hàm tự động chạy khi file FXML được nạp thành công.
     * Dùng để thiết lập cấu trúc bảng và sự kiện nút bấm.
     */
    @FXML
    private Button btnCreate; // Khai báo nút Create từ FXML

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        setupLinkActions();

        // Gán sự kiện mở form tạo task
        btnCreate.setOnAction(event -> {
            CreateTaskController controller = ViewNavigator.getInstance().loadSubScene("/scenes/CreateTask.fxml");
            if (controller != null) {
                controller.setProject(this.project); // Truyền thông tin project hiện tại sang
            }
        });
    }

    @Override
    public void renderData(Project project, String adminName) {
//        System.out.println(project.getProjectName() + " " + adminName);
        this.project = project;
        if (project != null) {
            loadData(project.getProjectId());
        }
    }

    /**
     * Thiết lập cách đổ dữ liệu từ Model Task vào từng cột của TableView.
     */
    private void setupTableColumns() {
        // --- XỬ LÝ CỘT NAME (Có Double-click) ---
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskName()));

        colName.setCellFactory(column -> {
            return new TableCell<PersonalTaskDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        // Style để người dùng biết có thể tương tác
                        setStyle("-fx-cursor: hand;");

                        // Sự kiện Double-click để xem chi tiết
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 2) {
                                PersonalTaskDTO clickedTask = getTableView().getItems().get(getIndex());
                                handleOpenTaskDetail(clickedTask);
                            }
                        });
                    }
                }
            };
        });

        // --- CÁC CỘT CÒN LẠI GIỮ NGUYÊN ---
        colAssignee.setCellValueFactory(cd -> {
            if (cd.getValue().getUser() != null) return new SimpleStringProperty(cd.getValue().getUser().getUserName());
            return new SimpleStringProperty("N/A");
        });
        colStart.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskStartTime()));
        colDeadline.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskEndTime()));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getStatusName()));
        colDescription.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskDescription()));
    }

    /**
     * Gắn sự kiện Click cho các Hyperlink để xử lý thay đổi giao diện/dữ liệu.
     */
    private void setupLinkActions() {
        // Mặc định chọn All khi mới vào
        setActiveLink(hlAll);

        // Khi nhấn All -> Hiện y hệt dữ liệu cũ
        hlAll.setOnAction(e -> {
            setActiveLink(hlAll);
            taskTable.setItems(masterData);
        });

        // Khi nhấn In preview -> Cũng hiện y hệt dữ liệu mới (chưa lọc nên vẫn để masterData)
        hlInPreview.setOnAction(e -> {
            setActiveLink(hlInPreview);
            taskTable.setItems(masterData);
        });
    }

    /**
     * Thay đổi Style Class để biểu thị nút nào đang được chọn (Active).
     * Kết hợp với CSS để tạo hiệu ứng màu nền capsule.
     */
    private void setActiveLink(Hyperlink selected) {
        hlAll.getStyleClass().remove("filter-item-active");
        hlInPreview.getStyleClass().remove("filter-item-active");

        selected.getStyleClass().add("filter-item-active");
    }


    /**
     * Gọi Service để lấy dữ liệu từ Database dựa trên Project ID.
     */
    public void loadData(int projectId) {
        masterData.setAll(service.getTasksByProject(projectId));
        taskTable.setItems(masterData);
    }

    /**
     * Điều hướng người dùng sang trang chi tiết công việc.
     * Kết hợp logic ViewNavigator có sẵn của bạn.
     */
    private void handleOpenTaskDetail(PersonalTaskDTO task) {
        try {
            Object controller = ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");

            if (controller instanceof TaskDetailController) {
                TaskDetailController detailController = (TaskDetailController) controller;
                detailController.setProjectId(project.getProjectId());

                PersonalTaskDTO dto = getPersonalTaskDTO(task);

                detailController.setTaskData(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PersonalTaskDTO getPersonalTaskDTO(PersonalTaskDTO task) {
        PersonalTaskDTO dto = new PersonalTaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setTaskDescription(task.getTaskDescription());
        dto.setTaskStartTime(task.getTaskStartTime());
        dto.setTaskEndTime(task.getTaskEndTime());
        dto.setStatusName(task.getStatusName());
        if (project != null) {
            dto.setProjectName(project.getProjectName());
        }
        return dto;
    }
}
