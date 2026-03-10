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

    public boolean createProjectWithManager(Project project, int adminId, int managerId) {
        return projectDAO.createProjectWithManagersTransaction(project, adminId, managerId);
    }

    public boolean deleteProject(int projectId) {
        return projectDAO.deleteByProjectId(projectId);
    }
}