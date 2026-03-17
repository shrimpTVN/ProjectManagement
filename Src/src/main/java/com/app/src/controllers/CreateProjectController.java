package com.app.src.controllers;

import com.app.src.core.AppContext;
import com.app.src.daos.UserDAO;
import com.app.src.models.Project;
import com.app.src.models.User;
import com.app.src.services.ProjectService;
// import com.app.src.daos.UserDAO;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

// Initializable để tự động gọi hàm initialize() sau khi load FXML
public class CreateProjectController implements Initializable {

    @FXML
    private TextField txtProjectName;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private TextArea txtDescription;

    @FXML
    private ComboBox<User> cbManager;

    @FXML
    private Button btnCreate;
    @FXML
    private Button btnCancel;

    @FXML
    private Label lblErrorName;
    @FXML
    private Label lblErrorStartDate;
    @FXML
    private Label lblErrorEndDate;

    private ProjectService projectService;
    private Project editingProject; // Biến để lưu thông tin dự án khi chỉnh sửa

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectService = new ProjectService();

        // Định dạng ComboBox chỉ hiển thị tên User
        cbManager.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getUserName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });

        // Load danh sách user từ Database lên ComboBox
        loadManagers();

        btnCreate.setOnAction(event -> handleCreate());
        btnCancel.setOnAction(event -> handleCancel());

        clearErrors();
    }

    private void loadManagers() {
        // Gọi UserDAO lấy danh sách và đẩy vào ComboBox
        List<User> managers = UserDAO.getInstance().findAll();
        cbManager.getItems().addAll(managers);
    }

    private void handleCreate() {
        System.out.println("Xử lý tạo hoặc cập nhật dự án...");
        clearErrors();

        // 1. Lấy dữ liệu cơ bản từ Form
        String name = txtProjectName.getText();
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        String description = txtDescription.getText();

        System.out.println("Dữ liệu trên form - Name: " + name + ", Start: " + startDate + ", End: " + endDate);

        // 2. Validate dữ liệu chung
        boolean isValid = true;
        if (name == null || name.trim().isEmpty()) {
            lblErrorName.setText("Vui lòng nhập tên dự án");
            isValid = false;
        }
        if (startDate == null) {
            lblErrorStartDate.setText("Vui lòng chọn ngày bắt đầu");
            isValid = false;
        }
        if (endDate == null) {
            lblErrorEndDate.setText("Vui lòng chọn ngày kết thúc");
            isValid = false;
        }

        if (!isValid) {
            System.out.println("Bị chặn lại vì thiếu dữ liệu trên form!");
            return; // Dừng lại nếu form thiếu
        }

        // 3. Đóng gói vào Object
        Project projectData = new Project();
        projectData.setProjectName(name);
        projectData.setProjectDescription(description);
        projectData.setProjectStartDate(java.sql.Date.valueOf(startDate));
        projectData.setProjectEndDate(java.sql.Date.valueOf(endDate));

        // 4. Phân luồng
        if (editingProject == null) {
            System.out.println("Đang chạy luồng: TẠO MỚI");
            // TẠO MỚI: Bắt buộc lấy Manager từ ComboBox
            User selectedManager = cbManager.getValue();
            if (selectedManager == null) {
                System.out.println("Lỗi: Chưa chọn Manager cho dự án mới");
                return;
            }

            int managerId = selectedManager.getUserId();
            int adminId = AppContext.getInstance().getUserData().getUserId();

            if (projectService.createProjectWithManager(projectData, adminId, managerId)) {
                finalizeAction("Tạo thành công!");
            }
        } else {
            System.out.println("Đang chạy luồng: EDIT - ID dự án: " + editingProject.getProjectId());
            // 1. Cập nhật thông tin cơ bản
            projectData.setProjectId(editingProject.getProjectId());
            boolean success = projectService.updateProject(projectData);

            // 2. Cập nhật Manager
            User selectedManager = cbManager.getValue();
            if (selectedManager != null) {
                projectService.updateProjectManager(editingProject.getProjectId(), selectedManager.getUserId());
            }

            System.out.println("Kết quả update từ database: " + success);

            if (success) {
                finalizeAction("Cập nhật thành công!");
            } else {
                System.out.println("Lỗi: Hàm updateProject trả về false.");
            }
        }
    }

    private void finalizeAction(String message) {
        System.out.println(message);
        AppContext.refreshProjects();
        if (SideBarController.getInstance() != null) {
            SideBarController.getInstance().reloadProjects();
        }
        ViewNavigator.getInstance().loadSubScene("/scenes/ProjectList.fxml");
    }

    private void handleCancel() {
        System.out.println("Hủy tạo dự án. Quay về màn hình Home.");
        ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
    }

    private void clearErrors() {
        lblErrorName.setText("");
        lblErrorStartDate.setText("");
        lblErrorEndDate.setText("");
    }

    public void setProjectInfo(Project project) {
        this.editingProject = project;
        txtProjectName.setText(project.getProjectName());

        if (project.getProjectStartDate() != null) {
            java.time.LocalDate localDate = new java.sql.Date(project.getProjectStartDate().getTime()).toLocalDate();
            dpStartDate.setValue(localDate);
        }

        if (project.getProjectEndDate() != null) {
            java.time.LocalDate localDate = new java.sql.Date(project.getProjectEndDate().getTime()).toLocalDate();
            dpEndDate.setValue(localDate);
        }

        // Đổi tên nút bấm để người dùng biết họ đang Lưu chứ không phải Tạo mới
        btnCreate.setText("Save Changes");

        txtDescription.setText(project.getProjectDescription());
    }
}