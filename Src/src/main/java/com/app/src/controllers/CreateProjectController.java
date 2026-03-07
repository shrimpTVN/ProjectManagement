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

    @FXML private TextField txtProjectName;
    @FXML private DatePicker dpStartDate;
    @FXML private DatePicker dpEndDate;
    @FXML private TextArea txtDescription;

    @FXML private ComboBox<User> cbManager;

    @FXML private Button btnCreate;
    @FXML private Button btnCancel;

    @FXML private Label lblErrorName;
    @FXML private Label lblErrorStartDate;
    @FXML private Label lblErrorEndDate;

    private ProjectService projectService;
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
        clearErrors();
        boolean isValid = true;     // Biến cờ để kiểm tra tính hợp lệ của form

        String name = txtProjectName.getText();
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        String description = txtDescription.getText();
        User selectedManager = cbManager.getValue(); // Lấy trực tiếp đối tượng User

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

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            lblErrorEndDate.setText("Ngày kết thúc phải lớn hơn ngày bắt đầu");
            isValid = false;
        }

        if (selectedManager == null) {
            System.out.println("Vui lòng chọn Manager");
            isValid = false;
        }

        if (isValid) {
            // Lấy ID từ user đã chọn
            int managerId = selectedManager.getUserId();

            // Đóng gói dữ liệu vào đối tượng Project
            Project newProject = new Project();
            newProject.setProjectName(name);
            newProject.setProjectStartDate(java.sql.Date.valueOf(startDate));
            newProject.setProjectEndDate(java.sql.Date.valueOf(endDate));
            newProject.setProjectDescription(description);

            // Gọi Service lưu dữ liệu
            try {
                boolean success = projectService.createProjectWithManager(newProject, managerId);

                if (success) {
                    System.out.println("Tạo dự án và gán Manager thành công!");
                    // Cập nhật danh sách projects trong AppContext
                    AppContext.refreshProjects();
                    // Chuyển về màn hình danh sách dự án
                    ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
                } else {
                    System.out.println("Có lỗi xảy ra khi tạo dự án. Service trả về false");
                }
            } catch (Exception e) {
                System.out.println("Có lỗi xảy ra khi tạo dự án: " + e.getMessage());
                e.printStackTrace();
            }
        }
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
}