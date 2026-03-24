package com.app.src.controllers;

import com.app.src.core.AppContext;
import com.app.src.daos.UserDAO;
import com.app.src.models.Project;
import com.app.src.models.User;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.ProjectService;
import com.app.src.controllers.project.ProjectDetailController;
import com.app.src.controllers.project.ProjectDetailNavigator;
import com.app.src.controllers.project.IProjectDetailSubView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ComboBox<String> cbManager;

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

    // Map username (Account) -> User để tra ngược khi lưu
    private final Map<String, User> managerMap = new HashMap<>();
    private final ObservableList<String> managerUsernames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectService = new ProjectService();

        // Cho phép gõ username và lọc theo Account username
        cbManager.setEditable(true);

        // Bắt lỗi DatePicker khi người dùng gõ sai định dạng yyyy-MM-dd
        setupDatePickerValidation(dpStartDate);
        setupDatePickerValidation(dpEndDate);

        loadManagers();

        btnCreate.setOnAction(event -> handleCreate());
        btnCancel.setOnAction(event -> handleCancel());

        clearErrors();
    }

    private void loadManagers() {
        managerMap.clear();
        managerUsernames.clear();

        List<User> basicUsers = UserDAO.getInstance().findAll();

        for (User user : basicUsers) {
            // Lấy bản đầy đủ để chắc chắn có Account
            User fullUser = UserDAO.getInstance().findById(user.getUserId());
            if (fullUser == null || fullUser.getAccount() == null) {
                continue;
            }

            String accountUsername = fullUser.getAccount().getUserName();
            managerMap.put(accountUsername, fullUser);
            managerUsernames.add(accountUsername);
        }

        FilteredList<String> filteredList = new FilteredList<>(managerUsernames, p -> true);
        cbManager.setItems(filteredList);

        cbManager.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = cbManager.getEditor();
            final String selected = cbManager.getSelectionModel().getSelectedItem();

            if (selected == null || !selected.equals(editor.getText())) {
                filteredList.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    return item.toLowerCase().contains(newValue.toLowerCase());
                });
                cbManager.show();
            }
        });
    }

    // Thiết lập converter và validate text nhập vào DatePicker
    private void setupDatePickerValidation(DatePicker datePicker) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? dateFormatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        return null; // gõ sai thì coi như rỗng
                    }
                }
                return null;
            }
        });

        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                try {
                    LocalDate.parse(datePicker.getEditor().getText(), dateFormatter);
                } catch (DateTimeParseException e) {
                    datePicker.getEditor().setText("");
                    datePicker.setValue(null);
                }
            }
        });
    }

    private void handleCreate() {
        System.out.println("Handling create/update project...");
        clearErrors();

        // 1. Read form data
        String name = txtProjectName.getText();
        LocalDate startDate = dpStartDate.getValue();
        LocalDate endDate = dpEndDate.getValue();
        String description = txtDescription.getText();

        System.out.println("Form data - Name: " + name + ", Start: " + startDate + ", End: " + endDate);

        // 2. Validate
        boolean isValid = true;
        if (name == null || name.trim().isEmpty()) {
            lblErrorName.setText("Please enter project name");
            isValid = false;
        }
        if (startDate == null) {
            lblErrorStartDate.setText("Please select start date");
            isValid = false;
        }
        if (endDate == null) {
            lblErrorEndDate.setText("Please select end date");
            isValid = false;
        }

        Date today = truncateToDate(new Date());
        Date startDateValue = toDate(startDate);
        Date endDateValue = toDate(endDate);

        if (startDateValue != null && endDateValue != null && endDateValue.before(startDateValue)) {
            lblErrorEndDate.setText("End date must be after start date");
            isValid = false;
        }
        if (startDateValue != null && startDateValue.before(today)) {
            lblErrorStartDate.setText("Start date cannot be before today");
            isValid = false;
        }
        if (endDateValue != null && endDateValue.before(today)) {
            lblErrorEndDate.setText("End date cannot be before today");
            isValid = false;
        }

        if (!isValid) {
            System.out.println("Blocked due to missing/invalid form data");
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
            System.out.println("Flow: CREATE NEW");
            // CREATE: manager is required
            String managerUsername = cbManager.getEditor().getText().trim();
            User selectedManager = managerMap.get(managerUsername);
            if (selectedManager == null) {
                System.out.println("Error: manager not selected or username not found");
                return;
            }

            int managerId = selectedManager.getUserId();
            int adminId = AppContext.getInstance().getUserData().getUserId();

            if (projectService.createProjectWithManager(projectData, adminId, managerId)) {
                finalizeAction("Created successfully!");
            }
        } else {
            System.out.println("Flow: EDIT - project ID: " + editingProject.getProjectId());
            // 1. Cập nhật thông tin cơ bản
            projectData.setProjectId(editingProject.getProjectId());
            boolean success = projectService.updateProject(projectData);

            System.out.println("Update result from database: " + success);

            if (success) {
                finalizeAction("Updated successfully!");
            } else {
                System.out.println("Error: updateProject returned false.");
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

    private void handleCancel() {   // Nếu đang edit thì quay về detail, nếu đang tạo mới thì về home
        if (editingProject != null) {
            Project fullProject = AppContext.getProjectById(editingProject.getProjectId());
            String adminName = new ProjectJoiningService().getAdmin(editingProject.getProjectId());

            ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");
            if (controller != null) {
                controller.renderData(fullProject, adminName);
                IProjectDetailSubView infoController = ProjectDetailNavigator.getInstance()
                        .loadSubView("/components/ProjectDetail/Infor.fxml");
                if (infoController != null) {
                    infoController.renderData(fullProject, adminName);
                }
            }
            return;
        }
        ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
    }

    private void clearErrors() {
        lblErrorName.setText("");
        lblErrorStartDate.setText("");
        lblErrorEndDate.setText("");
    }

    private Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return truncateToDate(java.sql.Date.valueOf(localDate));
    }

    private Date truncateToDate(Date date) {
        if (date == null) return null;
        return new java.sql.Date(date.getTime());
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

        // Không cho đổi manager khi edit
        cbManager.setDisable(true);
        cbManager.setEditable(false);
        cbManager.setPromptText("Manager unchanged on edit");

        txtDescription.setText(project.getProjectDescription());
    }
}