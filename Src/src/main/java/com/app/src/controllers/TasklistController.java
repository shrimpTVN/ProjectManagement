package com.app.src.controllers;

import com.app.src.models.Task;
import com.app.src.services.TasklistService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.sql.Time;

public class TasklistController {

    // ==========================================
    // KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN (@FXML)
    // ==========================================
    @FXML private HBox breadcrumbBox;
    @FXML private Hyperlink hlHome;
    @FXML private Label lblCurrentPage;
    @FXML private Label lblTitle;

    @FXML private Button btnAll;
    @FXML private Button btnTodo;
    @FXML private Button btnInprogress;
    @FXML private Button btnInpreview;
    @FXML private Button btnDone;

    @FXML private TableView<Task> taskTable;
    @FXML private TableColumn<Task, String> colName;
    @FXML private TableColumn<Task, String> colProject;     // Tạm thời để trống
    @FXML private TableColumn<Task, Time> colStart;         // Dùng java.sql.Time theo DAO của bạn
    @FXML private TableColumn<Task, Time> colDeadline;      // Dùng java.sql.Time
    @FXML private TableColumn<Task, String> colStatus;      // Tạm thời để trống
    @FXML private TableColumn<Task, String> colDescription;

    // ==========================================
    // KHAI BÁO DỮ LIỆU VÀ SERVICE
    // ==========================================
    private TasklistService taskListService;
    private ObservableList<Task> masterTaskList;
    private FilteredList<Task> filteredData;

    public TasklistController() {
        this.taskListService = new TasklistService();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDataFromDatabase();
        setupFilterButtons();
        setupNavigation();
    }

    // ==========================================
    // MAP DỮ LIỆU VÀO CỘT
    // ==========================================
    private void setupTableColumns() {
        // Chỉ map những trường đang có thật trong class Task
        // Lưu ý: Tên trong ngoặc kép là tên BIẾN (VD: taskName), không phải tên hàm get (getTaskName)
        colName.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("taskStartTime"));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("taskEndTime"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));

        // colProject và colStatus chưa map nên sẽ hiển thị trống trên giao diện
    }

    private void loadDataFromDatabase() {
        // Lấy dữ liệu từ DB
        masterTaskList = FXCollections.observableArrayList(taskListService.getAllTasks());

        filteredData = new FilteredList<>(masterTaskList, p -> true);

        SortedList<Task> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(taskTable.comparatorProperty());

        taskTable.setItems(sortedData);
    }

    // ==========================================
    // XỬ LÝ SỰ KIỆN GIAO DIỆN
    // ==========================================
    private void setupFilterButtons() {
        btnAll.setOnAction(e -> { applyFilter("All"); setActiveButton(btnAll); });
        btnTodo.setOnAction(e -> { applyFilter("To do"); setActiveButton(btnTodo); });
        btnInprogress.setOnAction(e -> { applyFilter("In progress"); setActiveButton(btnInprogress); });
        btnInpreview.setOnAction(e -> { applyFilter("In preview"); setActiveButton(btnInpreview); });
        btnDone.setOnAction(e -> { applyFilter("Done"); setActiveButton(btnDone); });
    }

    private void applyFilter(String status) {
        filteredData.setPredicate(task -> {
            // TẠM THỜI: Luôn trả về true (hiển thị tất cả) vì chưa xử lý trường Status
            return true;
        });
    }

    private void setActiveButton(Button activeBtn) {
        String normalStyle = "-fx-background-color: transparent; -fx-padding: 0; -fx-font-weight: normal;";
        String activeStyle = "-fx-background-color: transparent; -fx-padding: 0; -fx-font-weight: 800;";

        btnAll.setStyle(normalStyle);
        btnTodo.setStyle(normalStyle);
        btnInprogress.setStyle(normalStyle);
        btnInpreview.setStyle(normalStyle);
        btnDone.setStyle(normalStyle);

        activeBtn.setStyle(activeStyle);
    }

    private void setupNavigation() {
        hlHome.setOnAction(e -> {
            System.out.println("Quay lại trang chủ...");
        });
    }
}