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
        int newProjectId = projectDAO.createAndReturnId(project);

        if (newProjectId > 0) {
            int adminRoleId = 2;    // role Admin có id là 2 (theo database)
            int managerRoleId = 1;  // role Project Manager có id là 1

            // Bước 3: Thêm Admin vào dự án
            boolean adminAdded = ProjectJoiningDAO.getInstance().assignRole(newProjectId, adminId, adminRoleId);
            if (!adminAdded) {
                return false;
            }

            // Bước 4: Thêm Manager vào dự án (nếu khác Admin)
            if (adminId != managerId) {
                boolean managerAdded = ProjectJoiningDAO.getInstance().assignRole(newProjectId, managerId, managerRoleId);
                return managerAdded;
            }

            return true;
        }

        return false;
    }

    public boolean deleteProject(int projectId) {
        return projectDAO.deleteByProjectId(projectId);
    }
}