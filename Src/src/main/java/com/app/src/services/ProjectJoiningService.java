package com.app.src.services;

import com.app.src.daos.ProjectJoiningDAO;
import com.app.src.models.ProjectJoining;

import java.util.ArrayList;

public class ProjectJoiningService {
    private static ProjectJoiningDAO projectJoiningDao;

    public ProjectJoiningService() {
        projectJoiningDao = ProjectJoiningDAO.getInstance();
    }

    public String getAdmin(int projectId) {
        return projectJoiningDao.getAdmin(projectId);
    }

    public ArrayList<ProjectJoining> findAllJoiningsByProjectId(int projectId) {
        return projectJoiningDao.findAllJoiningsByProjectId(projectId);
    }

    public boolean updateRole(int projectId, int userId, int newRoleId) {
        return projectJoiningDao.updateRole(projectId, userId, newRoleId);
    }
}
