package com.app.src.controllers.task;

import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.services.TasklistService;
import com.app.src.core.AppContext;
import com.app.src.models.User;
import com.app.src.controllers.ViewNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;

import java.util.List;
import java.util.Locale;

public class TasklistController {

    // ==========================================
    // KHAI BÁO CÁC THÀNH PHẦN GIAO DIỆN (@FXML)
    // ==========================================
    @FXML private Label lblTitle;

    @FXML private Button btnAll;
    @FXML private Button btnTodo;
    @FXML private Button btnInprogress;
    @FXML private Button btnInpreview;
    @FXML private Button btnDone;

    @FXML private TableView<PersonalTaskDTO> taskTable;
    @FXML private TableColumn<PersonalTaskDTO, String> colName;
    @FXML private TableColumn<PersonalTaskDTO, String> colProject;
    @FXML private TableColumn<PersonalTaskDTO, String> colStart;
    @FXML private TableColumn<PersonalTaskDTO, String> colDeadline;
    @FXML private TableColumn<PersonalTaskDTO, String> colStatus;
    @FXML private TableColumn<PersonalTaskDTO, String> colDescription;

    // ==========================================
    // KHAI BÁO DỮ LIỆU VÀ SERVICE
    // ==========================================
    private TasklistService taskListService;
    private ObservableList<PersonalTaskDTO> masterTaskList;
    private FilteredList<PersonalTaskDTO> filteredData;

    private int currentUserId;

    private static final String FILTER_BTN_BASE_STYLE =
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent transparent transparent transparent;" +
            "-fx-border-width: 0 0 2 0;" +
            "-fx-background-insets: 0;" +
            "-fx-border-insets: 0;" +
            "-fx-padding: 0;" +
            "-fx-font-family: 'Urbanist Medium';" +
            "-fx-font-size: 20px;" +
            "-fx-text-fill: #666666;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;";

    private static final String FILTER_BTN_ACTIVE_STYLE =
            FILTER_BTN_BASE_STYLE +
            "-fx-text-fill: #111111;" +
            "-fx-border-color: transparent transparent #111111 transparent;" +
            "-fx-border-width: 0 0 2 0;";

    public TasklistController() {
        this.taskListService = new TasklistService();
    }

    @FXML
    public void initialize() {
        User currentUser = AppContext.getInstance().getUserData();
        // Lưu user hiện tại để dùng khi cập nhật status có kiểm tra quyền.
        currentUserId = currentUser.getUserId();

        setupTableColumns();
        taskTable.setEditable(false);
        taskTable.setRowFactory(tv -> {
            TableRow<PersonalTaskDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Kiểm tra: Nếu là click đúp (clickCount == 2) và dòng đó có dữ liệu (không click vào vùng trống)
                if (event.getClickCount() == 2 && (!row.isEmpty())) {

                    // 1. Lấy dữ liệu của dòng được click
                    PersonalTaskDTO clickedTask = row.getItem();

//                    System.out.println("Bạn vừa double-click vào Task: " + clickedTask.getTaskName());

                    // 2. Chuyển sang Scene khác (Sử dụng ViewNavigator của bạn)
                    // Chú ý: Bạn cần truyền dữ liệu của clickedTask sang màn hình mới để hiển thị
                    openTaskDetailScene(clickedTask);
                }
            });
            return row;
        });
        loadDataFromDatabase(currentUserId);
        setupFilterButtons();
    }
    private void openTaskDetailScene(PersonalTaskDTO clickedTask) {
//        ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");

        try {
            TaskDetailController detailController = ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");
            detailController.setTaskData(clickedTask);
            detailController.setProjectId(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(toDisplayStatus(cellData.getValue().getStatusName())));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskDescription()));
    }

    // ==========================================
    // 2. LẤY DỮ LIỆU TỪ DB VÀ ĐỔ VÀO BẢNG
    // ==========================================
    private void loadDataFromDatabase( int userID) {
        // Bước 1: Gọi Service để lấy danh sách Task từ Database (List Java thông thường)
        List<PersonalTaskDTO> tasksFromDB = taskListService.getTaskByUser(userID);
        for (PersonalTaskDTO task : tasksFromDB) {
            task.setStatusName(toDisplayStatus(task.getStatusName()));
        }

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

        btnAll.setOnAction(e -> { applyFilter("All"); setActiveButton(btnAll); });
        btnTodo.setOnAction(e -> { applyFilter("To do"); setActiveButton(btnTodo); });
        btnInprogress.setOnAction(e -> { applyFilter("In progress"); setActiveButton(btnInprogress); });
        btnInpreview.setOnAction(e -> { applyFilter("In preview"); setActiveButton(btnInpreview); });
        btnDone.setOnAction(e -> { applyFilter("Done"); setActiveButton(btnDone); });
    }

    private void applyFilter(String status) {
        if (filteredData == null) {
            return;
        }

        filteredData.setPredicate(task -> {
            if ("All".equalsIgnoreCase(status)) {
                return true;
            }

            String taskStatus = normalizeStatus(task.getStatusName());
            String targetStatus = normalizeStatus(status);
            return taskStatus.equals(targetStatus);
        });
    }

    private String normalizeStatus(String rawStatus) {
        if (rawStatus == null) {
            return "";
        }

        String normalized = rawStatus.trim().toLowerCase(Locale.ROOT);
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

    private String toDisplayStatus(String rawStatus) {
        String normalized = normalizeStatus(rawStatus);
        return switch (normalized) {
            case "to do" -> "To Do";
            case "in progress" -> "In Progress";
            case "in preview" -> "In Preview";
            case "done" -> "Done";
            default -> rawStatus == null ? "To Do" : rawStatus;
        };
    }

    // ==========================================
    // 4. CHỈNH STYLE NÚT BẤM VÀ ĐIỀU HƯỚNG
    // ==========================================
    private void setActiveButton(Button activeBtn) {
        btnAll.setStyle(FILTER_BTN_BASE_STYLE);
        btnTodo.setStyle(FILTER_BTN_BASE_STYLE);
        btnInprogress.setStyle(FILTER_BTN_BASE_STYLE);
        btnInpreview.setStyle(FILTER_BTN_BASE_STYLE);
        btnDone.setStyle(FILTER_BTN_BASE_STYLE);

        activeBtn.setStyle(FILTER_BTN_ACTIVE_STYLE);
    }
}
