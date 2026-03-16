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
import com.app.src.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTaskController {
    @FXML private TextField txtName;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private Label lblErrorName;

    // Sửa FXML ID thành cbAssignee
    @FXML private ComboBox<String> cbAssignee;
    @FXML private Label lblErrorAssignee;

    private Project currentProject;

    // Lưu trữ thông tin thành viên để đối chiếu khi lưu
    private Map<String, User> projectMembersMap = new HashMap<>();
    private ObservableList<String> memberUsernames = FXCollections.observableArrayList();

    public void setProject(Project project) {
        this.currentProject = project;
        loadProjectMembers();
    }

    private void loadProjectMembers() {
        if (currentProject == null) return;

        // Dùng ProjectJoiningService thay vì UserDAO
        ProjectJoiningService joiningService = new ProjectJoiningService();
        List<ProjectJoining> joinings = joiningService.findAllJoiningsByProjectId(currentProject.getProjectId());

        for (ProjectJoining joining : joinings) {
            User u = joining.getUser();
            if (u != null) {
                // Tùy thuộc vào việc ProjectJoiningDAO của bạn có join với bảng Account hay không.
                // Nếu có, dùng: String displayName = u.getAccount().getUserName();
                // Nếu chưa có, tạm thời dùng Full Name:
                String displayName = u.getUserName();

                projectMembersMap.put(displayName, u);
                memberUsernames.add(displayName);
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