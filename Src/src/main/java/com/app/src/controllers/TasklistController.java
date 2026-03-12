package com.app.src.controllers;

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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.control.TableRow;
import javafx.fxml.FXMLLoader;

import java.util.List;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import java.io.IOException;

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

    public TasklistController() {
        this.taskListService = new TasklistService();
    }

    @FXML
    public void initialize() {
        User currentUser = AppContext.getInstance().getUserData();
        setupTableColumns();
        taskTable.setRowFactory(tv -> {
            TableRow<PersonalTaskDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                // Kiểm tra: Nếu là click đúp (clickCount == 2) và dòng đó có dữ liệu (không click vào vùng trống)
                if (event.getClickCount() == 2 && (!row.isEmpty())) {

                    // 1. Lấy dữ liệu của dòng được click
                    PersonalTaskDTO clickedTask = row.getItem();

                    System.out.println("Bạn vừa double-click vào Task: " + clickedTask.getTaskName());

                    // 2. Chuyển sang Scene khác (Sử dụng ViewNavigator của bạn)
                    // Chú ý: Bạn cần truyền dữ liệu của clickedTask sang màn hình mới để hiển thị
                    openTaskDetailScene(clickedTask);
                }
            });
            return row;
        });
        loadDataFromDatabase(currentUser.getUserId());
        setupFilterButtons();
        setupNavigation();
    }
    private void openTaskDetailScene(PersonalTaskDTO clickedTask) {
//        ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");

        try {

            TaskDetailController detailController = ViewNavigator.getInstance().loadSubScene("/scenes/detailinfotask.fxml");

            detailController.setTaskData(clickedTask);

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

        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusName()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTaskDescription()));
    }

    // ==========================================
    // 2. LẤY DỮ LIỆU TỪ DB VÀ ĐỔ VÀO BẢNG
    // ==========================================
    private void loadDataFromDatabase( int userID) {
        // Bước 1: Gọi Service để lấy danh sách Task từ Database (List Java thông thường)
        List<PersonalTaskDTO> tasksFromDB = taskListService.getTaskByUser(userID);

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
            if (task.getStatusName() != null) {
                return task.getStatusName().equalsIgnoreCase(status);
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