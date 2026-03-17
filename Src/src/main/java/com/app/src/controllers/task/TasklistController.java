package com.app.src.controllers.task;

import com.app.src.dtos.PersonalTaskDTO;
import com.app.src.services.TasklistService;
import com.app.src.core.AppContext;
import com.app.src.models.User;
//import com.app.src.controllers.SceneManager;
import com.app.src.controllers.ViewNavigator;
import javafx.beans.property.SimpleStringProperty; // Import thêm cái này cho Lambda
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.List;
import java.util.Locale;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import java.io.IOException;

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

    private static final ObservableList<String> STATUS_OPTIONS = FXCollections.observableArrayList(
            "To Do", "In Progress", "In Preview"
    );

    public TasklistController() {
        this.taskListService = new TasklistService();
    }

    @FXML
    public void initialize() {
        User currentUser = AppContext.getInstance().getUserData();
        // Lưu user hiện tại để dùng khi cập nhật status có kiểm tra quyền.
        currentUserId = currentUser.getUserId();

        setupTableColumns();
        taskTable.setEditable(true);
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

        colStatus.setCellFactory(column -> new TableCell<>() {
            private final ComboBox<String> statusBox = new ComboBox<>(STATUS_OPTIONS);
            private boolean suppressAction;

            {
                statusBox.setMaxWidth(Double.MAX_VALUE);
                statusBox.setOnAction(event -> {
                    if (suppressAction || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        return;
                    }

                    PersonalTaskDTO task = getTableView().getItems().get(getIndex());
                    if (task == null) {
                        return;
                    }

                    String newStatus = statusBox.getValue();
                    String oldStatus = toDisplayStatus(task.getStatusName());
                    if (newStatus == null || oldStatus.equalsIgnoreCase(newStatus)) {
                        return;
                    }

                    // Xác nhận nhỏ trước khi đổi trạng thái.
                    if (!confirmStatusChange(task.getTaskName(), oldStatus, newStatus)) {
                        suppressAction = true;
                        statusBox.setValue(oldStatus);
                        applyStatusColor(statusBox, oldStatus);
                        suppressAction = false;
                        return;
                    }

                    try {
                        String content = "Cap nhat trang thai tu TaskList: " + oldStatus + " -> " + newStatus;
                        boolean updated = taskListService.updateTaskStatus(task.getTaskId(), oldStatus, newStatus, content, currentUserId);
                        if (updated) {
                            task.setStatusName(newStatus);
                            applyStatusColor(statusBox, newStatus);
                            taskTable.refresh();
                            // Hiển thị toast thành công sau khi cập nhật.
                            showToast("Updated successfully");
                        } else {
                            suppressAction = true;
                            statusBox.setValue(oldStatus);
                            applyStatusColor(statusBox, oldStatus);
                            suppressAction = false;
                        }
                    } catch (Exception ex) {
                        suppressAction = true;
                        statusBox.setValue(oldStatus);
                        applyStatusColor(statusBox, oldStatus);
                        suppressAction = false;

                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Thong bao");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText(ex.getMessage());
                        errorAlert.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                String statusText = toDisplayStatus(item);
                suppressAction = true;
                statusBox.setValue(statusText);
                applyStatusColor(statusBox, statusText);
                suppressAction = false;
                setGraphic(statusBox);
            }
        });
    }

    // Hỏi xác nhận trước khi đổi trạng thái task.
    private boolean confirmStatusChange(String taskName, String oldStatus, String newStatus) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xac nhan");
        confirm.setHeaderText("Cap nhat trang thai task");
        confirm.setContentText("Task: " + taskName + "\n" + oldStatus + " -> " + newStatus + "\nBan co chac chan khong?");
        return confirm.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    // Tô màu theo trạng thái ngay trong ô status.
    private void applyStatusColor(ComboBox<String> statusBox, String status) {
        String normalized = normalizeStatus(status);
        String style = "-fx-font-family: 'Urbanist Medium'; -fx-font-size: 12px; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 0 4 0 4;";

        if ("to do".equals(normalized)) {
            style += "-fx-background-color: #FFF3CD; -fx-border-color: #E6C65C; -fx-text-fill: #8A6D00;";
        } else if ("in progress".equals(normalized)) {
            style += "-fx-background-color: #DCEBFF; -fx-border-color: #8BB6F2; -fx-text-fill: #1F5FAF;";
        } else if ("in preview".equals(normalized)) {
            style += "-fx-background-color: #EBDDFF; -fx-border-color: #C8A3F0; -fx-text-fill: #6B2AA6;";
        } else if ("done".equals(normalized)) {
            style += "-fx-background-color: #DDF5E3; -fx-border-color: #8EC89F; -fx-text-fill: #1F7A36;";
        }

        statusBox.setStyle(style);
    }

    // Toast đơn giản để báo cập nhật thành công.
    private void showToast(String message) {
        if (taskTable.getScene() == null || taskTable.getScene().getWindow() == null) {
            return;
        }

        Label toastLabel = new Label(message);
        toastLabel.setStyle("-fx-background-color: rgba(30,30,30,0.92); -fx-text-fill: white; -fx-padding: 8 14 8 14; -fx-background-radius: 12; -fx-font-family: 'Urbanist Medium';");

        Popup popup = new Popup();
        popup.getContent().add(toastLabel);
        popup.setAutoHide(true);

        Window window = taskTable.getScene().getWindow();
        popup.show(window);

        // Đặt toast ở gần góc dưới bên phải vùng cửa sổ.
        double x = window.getX() + window.getWidth() - toastLabel.getWidth() - 40;
        double y = window.getY() + window.getHeight() - 90;
        popup.setX(x);
        popup.setY(y);

        PauseTransition delay = new PauseTransition(Duration.seconds(1.4));
        delay.setOnFinished(event -> popup.hide());
        delay.play();
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

    private void setupNavigation() {
        hlHome.setOnAction(e -> {
            System.out.println("Đang chuyển hướng về trang chủ...");
            // Code chuyển Scene (FXML) sẽ nằm ở đây
        });
    }
}















