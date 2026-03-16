package com.app.src.core;

import com.app.src.core.session.UserSession;
import com.app.src.models.Project;
import com.app.src.models.User;
import com.app.src.services.ProjectService;

import java.util.ArrayList;

public class AppContext {

    private static AppContext instance;
    private static ArrayList<Project> projects;
    private final UserSession userSession;


    private AppContext() {
        userSession = UserSession.getInstance();
        ProjectService projectService = new ProjectService();
       projects =  ProjectService.getAllProjects(userSession.getUser().getUserId());
//        projects = ProjectService.getAllProjects(3);
    }


    public static AppContext getInstance() {
        if (instance == null) {
            instance = new AppContext();
        }

        return instance;
    }

    public static ArrayList<Project> getProjects() {
        return projects;
    }

    public static void refreshProjects() {      // cập nhật lại danh sách dự án sau khi có sự thay đổi (thêm, sửa, xóa dự án)
        UserSession userSession = UserSession.getInstance();
        projects = ProjectService.getAllProjects(userSession.getUser().getUserId());
    }

    public User getUserData() {
        return userSession.getUser();
    }

}
