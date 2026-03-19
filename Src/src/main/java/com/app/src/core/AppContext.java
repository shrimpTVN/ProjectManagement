package com.app.src.core;

import com.app.src.core.session.UserSession;
import com.app.src.models.Project;
import com.app.src.models.User;
import com.app.src.services.ProjectJoiningService;
import com.app.src.services.ProjectService;

import java.util.ArrayList;

public class AppContext {

    private static AppContext instance;
    private static ArrayList<Project> projects;
    private static UserSession userSession;


    private AppContext() {
        userSession = UserSession.getInstance();
        refreshProjects();
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
        if (userSession == null || userSession.getUser() == null) {
            return; // no user in session yet; skip refresh to avoid NPE
        }
        projects = ProjectService.getAllProjects(userSession.getUser().getUserId());
        ProjectJoiningService projectJoiningService = new ProjectJoiningService();
        // set user's role in each project
        for (Project project : projects) {
            project.setUserRoleName(projectJoiningService.getRoleInProject(userSession.getUser().getUserId(), project.getProjectId()));
        }
    }

    public static User getUserData() {
        return userSession.getUser();
    }

    public static Project getProjectById(int projectId){
        for(Project project : projects){
            if(project.getProjectId() == projectId){
                return project;
            }
        }

        return new Project();
    }
}
