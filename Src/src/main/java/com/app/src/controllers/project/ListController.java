package com.app.src.controllers.project;

import com.app.src.controllers.TaskDetailController;
import com.app.src.controllers.ViewNavigator;
import com.app.src.core.AppContext;
import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.models.Project;
import javafx.scene.control.Hyperlink;
import com.app.src.models.Task;
import com.app.src.services.TasklistService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListController implements IProjectDetailSubView {


    // ==========================================
    // KHAI BÁO THÀNH PHẦN GIAO DIỆN (UI)
    // ==========================================
    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colName, colAssignee, colStart, colDeadline, colStatus, colDescription;
    @FXML private Hyperlink hlAll, hlInPreview;

    // ==========================================
    // KHAI BÁO DỮ LIỆU & SERVICE
    // ==========================================
    private TasklistService service = new TasklistService();
    private ObservableList<Task> masterData = FXCollections.observableArrayList();
    private Project project;

    /**
     * Hàm tự động chạy khi file FXML được nạp thành công.
     * Dùng để thiết lập cấu trúc bảng và sự kiện nút bấm.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupLinkActions();
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
            return new TableCell<Task, String>() {
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
                                Task clickedTask = getTableView().getItems().get(getIndex());
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
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskStatus()));
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
    private void handleOpenTaskDetail(Task task) {
        try {
            Object controller = ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");

            if (controller instanceof TaskDetailController) {
                TaskDetailController detailController = (TaskDetailController) controller;
                detailController.setProjectId(project.getProjectId());

                // --- BƯỚC CẦU NỐI: Chuyển Task sang PersonalTaskDTO ---
                PersonalTaskDTO dto = new PersonalTaskDTO();
                dto.setTaskName(task.getTaskName());
                dto.setTaskDescription(task.getTaskDescription());
                dto.setTaskStartTime(task.getTaskStartTime());
                dto.setTaskEndTime(task.getTaskEndTime());
                dto.setStatusName(task.getTaskStatus());
                if (project != null) {
                    dto.setProjectName(project.getProjectName());
                }
                // Map các trường khác nếu cần...

                // Bây giờ truyền DTO đi sẽ không còn lỗi nữa
                detailController.setTaskData(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
