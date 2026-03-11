package com.app.src.controllers.project;

import com.app.src.models.Project;
import javafx.scene.control.Hyperlink;
import com.app.src.models.Task;
import com.app.src.services.TasklistService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ListController implements IProjectDetailSubView {
    @Override
    public void renderData(Project project, String adminName) {
        System.out.println(project.getProjectName() + " " + adminName);
        if (project != null) {
            loadData(project.getProjectId());
        }
    }

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


    /**
     * Hàm tự động chạy khi file FXML được nạp thành công.
     * Dùng để thiết lập cấu trúc bảng và sự kiện nút bấm.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupLinkActions();
    }

    /**
     * Thiết lập cách đổ dữ liệu từ Model Task vào từng cột của TableView.
     */
    private void setupTableColumns() {
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTaskName()));
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
}
