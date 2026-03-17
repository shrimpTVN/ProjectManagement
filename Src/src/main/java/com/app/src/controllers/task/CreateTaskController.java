package com.app.src.controllers.task;

import com.app.src.controllers.ViewNavigator;
import com.app.src.controllers.project.ProjectDetailController;
import com.app.src.daos.ProjectDAO;
import com.app.src.daos.UserDAO;
import com.app.src.models.Project;
import com.app.src.models.ProjectJoining;
import com.app.src.models.Task;
import com.app.src.models.User;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.TasklistService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTaskController {
    // Lưu trữ thông tin thành viên để đối chiếu khi lưu
    private final Map<String, User> projectMembersMap = new HashMap<>();
    private final ObservableList<String> memberUsernames = FXCollections.observableArrayList();
    @FXML
    private TextField txtName;
    @FXML
    private TextArea txtDescription;
    @FXML
    private DatePicker dpStart;
    @FXML
    private DatePicker dpEnd;
    @FXML
    private Label lblErrorName;
    @FXML
    private ComboBox<String> cbAssignee;
    @FXML
    private Label lblErrorAssignee;
    private Project currentProject;

    @FXML
    public void initialize() {
        // Cấu hình DatePicker để bắt lỗi khi gõ bậy
        setupDatePickerValidation(dpStart);
        setupDatePickerValidation(dpEnd);
    }

    // Hàm thiết lập bộ chuyển đổi an toàn cho DatePicker
    private void setupDatePickerValidation(DatePicker datePicker) { // Cấu hình DatePicker để bắt lỗi khi gõ bậy
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.trim().isEmpty()) {
                    try {
                        // Thử parse chuỗi người dùng nhập
                        return LocalDate.parse(string, dateFormatter);
                    } catch (DateTimeParseException e) {
                        // Nếu gõ bậy (vd: "dsd"), bắt lỗi ở đây và trả về null (xóa ô nhập)
                        return null;
                    }
                }
                return null;
            }
        });

        // Tuỳ chọn thêm: Làm sạch text khi người dùng click ra ngoài (mất focus) mà nhập sai
        datePicker.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // Khi mất focus
                try {
                    // Cố ép kiểu text hiện tại, nếu lỗi thì tự động xóa
                    LocalDate.parse(datePicker.getEditor().getText(), dateFormatter);
                } catch (DateTimeParseException e) {
                    datePicker.getEditor().setText("");
                    datePicker.setValue(null);
                }
            }
        });
    }

    public void setProject(Project project) {
        this.currentProject = project;
        loadProjectMembers();
    }

    private void loadProjectMembers() {
        if (currentProject == null) return;

        ProjectJoiningService joiningService = new ProjectJoiningService();
        List<ProjectJoining> joinings = joiningService.findAllJoiningsByProjectId(currentProject.getProjectId());

        for (ProjectJoining joining : joinings) {
            User basicUser = joining.getUser();
            if (basicUser != null) {
                // Gọi UserDAO lấy thông tin User đầy đủ (bao gồm cả Account)
                User fullUser = UserDAO.getInstance().findById(basicUser.getUserId());

                // Bắt buộc phải có Account thì mới cho vào danh sách
                if (fullUser != null && fullUser.getAccount() != null) {
                    String accountUsername = fullUser.getAccount().getUserName();

                    projectMembersMap.put(accountUsername, fullUser);   // Lưu fullUser vào map với key là username của Account
                    memberUsernames.add(accountUsername);   // Thêm username vào ObservableList để hiển thị trong ComboBox
                }
            }
        }

        // Tạo danh sách lọc (FilteredList) cho ComboBox
        FilteredList<String> filteredList = new FilteredList<>(memberUsernames, p -> true);
        cbAssignee.setItems(filteredList);

        // Xử lý sự kiện gõ chữ để gợi ý
        cbAssignee.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = cbAssignee.getEditor();
            final String selected = cbAssignee.getSelectionModel().getSelectedItem();

            if (selected == null || !selected.equals(editor.getText())) {
                filteredList.setPredicate(item -> {
                    if (newValue == null || newValue.isEmpty()) return true;
                    return item.toLowerCase().contains(newValue.toLowerCase());
                });
                cbAssignee.show();
            }
        });
    }

    @FXML
    private void handleSaveTask() {
        lblErrorName.setText("");
        lblErrorAssignee.setText("");

        Task newTask = new Task();
        newTask.setTaskName(txtName.getText().trim());
        newTask.setTaskDescription(txtDescription.getText());

        if (dpStart.getValue() != null) newTask.setTaskStartTime(dpStart.getValue().toString());
        if (dpEnd.getValue() != null) newTask.setTaskEndTime(dpEnd.getValue().toString());

        if (currentProject != null) {
            newTask.setProjectId(currentProject.getProjectId());
        }

        // BẮT BUỘC CHỌN NGƯỜI
        String assigneeUsername = cbAssignee.getEditor().getText().trim();
        if (assigneeUsername.isEmpty()) {
            lblErrorAssignee.setText("Assignee is required!");
            return;
        }

        User foundUser = projectMembersMap.get(assigneeUsername);
        if (foundUser != null) {
            newTask.setUser(foundUser);
        } else {
            lblErrorAssignee.setText("User not found in this project!");
            return;
        }

        // GỌI SERVICE LƯU
        try {
            TasklistService service = new TasklistService();
            if (service.addTask(newTask)) {
                backToProjectDetail();
            } else {
                lblErrorName.setText("System error: Unable to create task.");
            }
        } catch (IllegalArgumentException e) {
            lblErrorName.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        backToProjectDetail();
    }

    private void backToProjectDetail() {
        if (currentProject == null) {
            ViewNavigator.getInstance().loadSubScene("/scenes/Home.fxml");
            return;
        }
        ProjectDetailController controller = ViewNavigator.getInstance().loadSubScene("/scenes/ProjectDetail.fxml");
        Project fullProject = ProjectDAO.getInstance().getProjectWithJoinings(currentProject.getProjectId());
        ProjectJoiningService joiningService = new ProjectJoiningService();
        String adminName = joiningService.getAdmin(currentProject.getProjectId());
        controller.renderData(fullProject, adminName);
    }
}