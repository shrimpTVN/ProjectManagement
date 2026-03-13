//package com.app.src.controllers;
//

//public class TaskDetailController {
//    private PersonalTaskDTO currentTask;
//
//    // Viết 1 hàm public để nhận dữ liệu truyền tới
//    public void setTaskData(PersonalTaskDTO task) {
//        this.currentTask = task;
//
//        // Nhận được data xong thì cập nhật lên giao diện luôn
//        System.out.println("Đã nhận được task: " + task.getTaskName());
//        // txtName.setText(task.getTaskName());
//    }
//
//}
//

package com.app.src.controllers;

import com.app.src.dtos.PersonalTaskDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;

public class TaskDetailController {

    // --- Các thành phần giao diện đã được khai báo trong file FXML ---

    // Breadcrumbs
    @FXML
    private Hyperlink hlHome;
    @FXML
    private Hyperlink hlProjectList;
    @FXML
    private Hyperlink hlProject;

    // Labels hiển thị thông tin chính
    @FXML
    private Label lblTask;
    @FXML
    private Label lblTaskName;
    @FXML
    private Label lblNameProject;
    @FXML
    private Label lblStart;
    @FXML
    private Label lblDeadline;
    @FXML
    private Label lblReporter; // (Hiện tại DTO chưa có trường này, tạm thời để trống hoặc lấy từ Session)
    @FXML
    private Label lblAuthor;   // (Tương tự lblReporter)
    @FXML
    private Label lblDescription;

    // Các nút bấm
    @FXML
    private MenuButton menubtnStatus;
    @FXML
    private Button btnCmt;
    @FXML
    private Button btnHistory;

    // Biến lưu trữ Task hiện tại đang xem
    private PersonalTaskDTO currentTask;

    /**
     * Hàm này tự động chạy khi giao diện được tải lên
     */
    @FXML
    public void initialize() {
        // Cài đặt sự kiện click cho các nút chuyển hướng (Breadcrumbs)
        hlHome.setOnAction(event -> {
            System.out.println("Quay về trang Home");
            SceneManager.getInstance().switchScene("/scenes/dashboard.fxml"); // Thay đường dẫn thật của bạn
        });

        hlProjectList.setOnAction(event -> {
            System.out.println("Quay về danh sách Dự án");
            // SceneManager.getInstance().switchScene("/scenes/project_list.fxml");
        });
    }

    /**
     * Hàm này được gọi từ TasklistController để truyền dữ liệu Task vào
     */
    public void setTaskData(PersonalTaskDTO task) {
        if (task == null) {
            System.err.println("Lỗi: Không nhận được dữ liệu Task!");
            return;
        }

        this.currentTask = task;

        // Đổ dữ liệu từ Object Task lên các Label trên màn hình
        lblTaskName.setText(task.getTaskName()); // Cập nhật tên Task (cho tiêu đề lớn)
        lblNameProject.setText(task.getProjectName() != null ? task.getProjectName() : "Chưa gắn dự án");

        // Cập nhật Breadcrumb Dự án
        hlProject.setText(task.getProjectName() != null ? task.getProjectName() : "No Project");

        lblStart.setText(task.getTaskStartTime() != null ? task.getTaskStartTime() : "Chưa xác định");
        lblDeadline.setText(task.getTaskEndTime() != null ? task.getTaskEndTime() : "Chưa xác định");

        lblDescription.setText(task.getTaskDescription() != null ? task.getTaskDescription() : "Không có mô tả.");

        // Cập nhật nút Dropdown Trạng thái
        menubtnStatus.setText(task.getStatusName() != null ? task.getStatusName() : "Trạng thái");

        // --- Phần này bạn cần bổ sung thêm nếu muốn hiển thị ---
        // lblReporter.setText("Người báo cáo...");
        // lblAuthor.setText(task.getUser().getUserName()); // Nếu Model của bạn hỗ trợ lấy người tạo

        // LOGIC TỰ ĐỘNG THU NHỎ FONT CHO TASK NAME
        String taskName = task.getTaskName();
        if (taskName.length() > 50) {
            lblTaskName.setStyle("-fx-font-size: 20px;"); // Rất dài -> font nhỏ
        } else if (taskName.length() > 25) {
            lblTaskName.setStyle("-fx-font-size: 28px;"); // Vừa phải -> font vừa
        } else {
            lblTaskName.setStyle("-fx-font-size: 36px;"); // Ngắn -> font to (mặc định)
        }
    }
}