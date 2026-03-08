package com.app.src.controllers;

import com.app.src.models.Task;
import com.app.src.services.TasklistService;

import javafx.beans.property.SimpleStringProperty; // Import thêm cái này cho Lambda
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.util.List;

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
    @FXML private TableColumn<Task, String> colProject;
    @FXML private TableColumn<Task, String> colStart;
    @FXML private TableColumn<Task, String> colDeadline;
    @FXML private TableColumn<Task, String> colStatus;
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
    // 1. MAP DỮ LIỆU VÀO CỘT (DÙNG LAMBDA ĐƠN GIẢN, KHÔNG LỖI MODULE)
    // ==========================================
    private void setupTableColumns() {
        // Thay vì dùng PropertyValueFactory dễ gây lỗi báo đỏ, ta dùng Lambda để gọi thẳng hàm getter.
        // cellData.getValue() chính là đối tượng Task của dòng hiện tại.

        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskName()));
        colProject.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProjectName()));

        // Giả sử hàm getTaskStartTime() và EndTime() trả về String, nếu nó trả về Date/Timestamp thì dùng String.valueOf()
        colStart.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTaskStartTime())));
        colDeadline.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTaskEndTime())));

        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskStatus()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskDescription()));
    }

    // ==========================================
    // 2. LẤY DỮ LIỆU TỪ DB VÀ ĐỔ VÀO BẢNG
    // ==========================================
    private void loadDataFromDatabase() {
        // Bước 1: Gọi Service để lấy danh sách Task từ Database (List Java thông thường)
        List<Task> tasksFromDB = taskListService.getAllTasks();

        // Bước 2: Ép kiểu List thường thành ObservableList.
        // Tính năng hay nhất của ObservableList là: khi danh sách này thay đổi (thêm/xóa), giao diện tự động cập nhật theo!
        masterTaskList = FXCollections.observableArrayList(tasksFromDB);

        // Bước 3: Đưa danh sách gốc vào FilteredList để chuẩn bị cho chức năng bấm nút lọc trạng thái
        filteredData = new FilteredList<>(masterTaskList, p -> true); // "p -> true" nghĩa là ban đầu hiển thị TẤT CẢ

        // Bước 4: Đổ cái danh sách đã gán bộ lọc này vào bảng để hiển thị lên màn hình
        taskTable.setItems(filteredData);
    }

    // ==========================================
    // 3. XỬ LÝ LỌC TRẠNG THÁI THEO NÚT BẤM
    // ==========================================
    private void setupFilterButtons() {
        setActiveButton(btnAll); // Mặc định tô đậm nút All khi mới mở app

        // Gắn sự kiện click cho từng nút
        btnAll.setOnAction(e -> { applyFilter("All"); setActiveButton(btnAll); });
        btnTodo.setOnAction(e -> { applyFilter("To do"); setActiveButton(btnTodo); });
        btnInprogress.setOnAction(e -> { applyFilter("In progress"); setActiveButton(btnInprogress); });
        btnInpreview.setOnAction(e -> { applyFilter("In preview"); setActiveButton(btnInpreview); });
        btnDone.setOnAction(e -> { applyFilter("Done"); setActiveButton(btnDone); });
    }

    private void applyFilter(String status) {
        // setPredicate giống như hàm if(), nếu trả về true thì dòng đó được hiện, false thì ẩn đi
        filteredData.setPredicate(task -> {
            if (status.equals("All")) {
                return true; // Nút All -> Hiện hết
            }

            // Kiểm tra xem trạng thái của task có khớp với nút vừa bấm không (bỏ qua viết hoa/thường)
            if (task.getTaskStatus() != null) {
                return task.getTaskStatus().equalsIgnoreCase(status);
            }
            return false;
        });
    }

    // ==========================================
    // 4. CHỈNH STYLE NÚT BẤM VÀ ĐIỀU HƯỚNG
    // ==========================================
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
            System.out.println("Đang chuyển hướng về trang chủ...");
            // Code chuyển Scene (FXML) sẽ nằm ở đây
        });
    }
}