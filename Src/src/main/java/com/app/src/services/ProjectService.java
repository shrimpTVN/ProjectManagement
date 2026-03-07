package com.app.src.services;

import com.app.src.daos.ProjectDAO;
import com.app.src.daos.ProjectJoiningDAO;
import com.app.src.models.Project;

import java.util.ArrayList;

public class ProjectService {
    private static ProjectDAO projectDAO;

    public ProjectService() {
        projectDAO = ProjectDAO.getInstance();
    }

    public static ArrayList<Project> getAllProjects(int userId) {
        return ProjectDAO.getInstance().findByUserId(userId);
    }

    public boolean createProjectWithManager(Project project, int managerId) {
        int newProjectId = projectDAO.createAndReturnId(project);

        if (newProjectId > 0) {
            int managerRoleId = 1;
            return ProjectJoiningDAO.getInstance().assignRole(newProjectId, managerId, managerRoleId);
        }

        return false;
    }
}