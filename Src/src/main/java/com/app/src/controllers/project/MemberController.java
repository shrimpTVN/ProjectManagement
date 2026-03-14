package com.app.src.controllers.project;

import com.app.src.models.Project;
import com.app.src.models.ProjectJoining;
import com.app.src.models.ProjectRole;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.ProjectRoleService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberController implements IProjectDetailSubView {
    @FXML
    private TextField txtUserName;

    @FXML
    private ComboBox<String> cbRole;

    @FXML
    private Button btnAddUser;

    @FXML
    private Button btnEditUser;

    @FXML
    private Button btnDeleteUser;

    @FXML
    private Button btnCancel;

    @FXML
    private TableView<ProjectJoining> memberTable;

    @FXML
    private TableColumn<ProjectJoining, String> colName;

    @FXML
    private TableColumn<ProjectJoining, String> colRole;

    @FXML
    private TableColumn<ProjectJoining, String> colDOB;

    @FXML
    private TableColumn<ProjectJoining, String> colPhone;

    private Project currentProject;
    private String adminName;
    private ProjectJoining selectedJoining;     // Biến để lưu User đang được chọn trong TableView
    private List<ProjectRole> allRoles;         // Biến toàn cục để lưu danh sách tất cả Role, giúp tìm ID khi cần

    @FXML
    public void initialize() {
        setupTableColumns();
        btnEditUser.setOnAction(event -> handleEditAction());
        btnCancel.setOnAction(event -> handleCancelAction());
        btnDeleteUser.setOnAction(event -> handleDeleteAction());
    }

    public void setupTableColumns() {
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUser().getUserName()));
        colRole.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRole().getRoleName()));
        colDOB.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUser().getUserDoB()));
        colPhone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUser().getUserPhoneNumber()));
    }

    @Override
    public void renderData(Project project, String adminName) {
        this.currentProject = project;
        this.adminName = adminName;
        if (loadMembers()) {
            System.out.println("Members Loaded");
        } else {
            System.out.println("Failed to load members");
        }
        loadRoleNames();
    }

    private void loadRoleNames() {
        ProjectRoleService roleService = new ProjectRoleService();
        allRoles = roleService.getAllRoles(); // Gán dữ liệu vào biến toàn cục

        List<String> roleNames = new ArrayList<>();
        for (ProjectRole item : allRoles) {
            roleNames.add(item.getRoleName());
        }
        cbRole.getItems().setAll(roleNames);
    }

    private boolean loadMembers() {
        // Đổi sang dùng ProjectJoiningService
        ProjectJoiningService joiningService = new ProjectJoiningService();
        if (currentProject != null) {
            try {
                // Lấy danh sách ProjectJoining theo mã dự án
                List<ProjectJoining> joinList = joiningService.findAllJoiningsByProjectId(currentProject.getProjectId());
                memberTable.getItems().setAll(joinList);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void handleEditAction() {
        // 1. Kiểm tra nếu nút đang ở trạng thái "Save" thì thực hiện lưu
        if (btnEditUser.getText().equals("Save")) {
            String selectedRoleName = cbRole.getValue();
            int newRoleId = -1;

            // Tìm ID Role tương ứng với tên được chọn trong ComboBox
            for (ProjectRole role : allRoles) {
                if (role.getRoleName().equals(selectedRoleName)) {
                    newRoleId = role.getRoleId();
                    break;
                }
            }

            // Gọi service để cập nhật cơ sở dữ liệu
            if (newRoleId != -1) {
                ProjectJoiningService service = new ProjectJoiningService();
                boolean success = service.updateRole(
                        currentProject.getProjectId(),
                        selectedJoining.getUser().getUserId(),
                        newRoleId
                );

                if (success) {
                    loadMembers(); // Tải lại bảng để hiện dữ liệu mới
                    clearForm();   // Dọn dẹp form
                } else {
                    System.out.println("Lỗi cập nhật CSDL.");
                }
            }
            return;
        }

        // 2. Lấy dòng dữ liệu đang được chọn dưới bảng
        selectedJoining = memberTable.getSelectionModel().getSelectedItem();

        // Nếu chưa chọn dòng nào thì không làm gì cả
        if (selectedJoining == null) {
            System.out.println("Vui lòng chọn một thành viên từ bảng.");
            return;
        }

        // 3. Đẩy dữ liệu lên form
        txtUserName.setText(selectedJoining.getUser().getUserName());
        txtUserName.setEditable(false); // Khóa ô UserName để tránh chỉnh sửa

        cbRole.setValue(selectedJoining.getRole().getRoleName()); // Đặt đúng role hiện tại

        // 4. Chuyển đổi giao diện sang chế độ chỉnh sửa
        btnEditUser.setText("Save");
        btnAddUser.setDisable(true); // Tạm ẩn hoặc khóa nút Add để tránh xung đột
    }

    private void handleCancelAction() {
        clearForm();
    }

    private void clearForm() {
        // Xóa trắng dữ liệu và mở khóa các ô nhập liệu
        txtUserName.clear();
        txtUserName.setEditable(true);

        cbRole.getSelectionModel().clearSelection();

        // Trả các nút về trạng thái ban đầu
        btnEditUser.setText("Edit");
        btnAddUser.setDisable(false);

        selectedJoining = null;
    }

    private void handleDeleteAction() {
        // Lấy dòng đang chọn
        ProjectJoining selected = memberTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("Vui lòng chọn một thành viên để xóa.");
            return;
        }

        // Hiển thị hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText("Bạn sắp xóa thành viên khỏi dự án");
        alert.setContentText("Xóa " + selected.getUser().getUserName() + " khỏi danh sách?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Gọi service thực hiện xóa
            ProjectJoiningService service = new ProjectJoiningService();
            boolean success = service.removeMember(
                    currentProject.getProjectId(),
                    selected.getUser().getUserId()
            );

            if (success) {
                loadMembers(); // Làm mới bảng
                clearForm();   // Dọn dẹp form phía trên (nếu đang bấm vào dòng đó)
            } else {
                System.out.println("Lỗi khi xóa thành viên.");
            }
        }
    }
}
