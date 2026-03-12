package com.app.src.controllers.project;

import com.app.src.models.Project;
import com.app.src.models.ProjectJoining;
import com.app.src.models.User;
import com.app.src.daos.ProjectJoiningDAO;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

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

    @FXML
    public void initialize() {
        setupTableColumns();
    }

    public void setupTableColumns(){
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUser().getUserName()));
        colRole.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getRole().getRoleName()));
        colDOB.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUser().getUserDoB()));
        colPhone.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getUser().getUserPhoneNumber()));
    }

    @Override
    public void renderData(Project project, String adminName) {
        this.currentProject = project;
        this.adminName = adminName;
        if(loadMembers()){
            System.out.println("Members Loaded");
        }else {
            System.out.println("Failed to load members");
        }
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
}
