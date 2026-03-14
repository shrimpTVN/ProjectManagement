package com.app.src.services;

import com.app.src.daos.ProjectRoleDAO;
import com.app.src.models.ProjectRole;

import java.util.List;

public class ProjectRoleService {
    private final ProjectRoleDAO projectRoleDao;

    public ProjectRoleService() {
        projectRoleDao = ProjectRoleDAO.getInstance();
    }

    public List<ProjectRole> getAllRoles() {
        return projectRoleDao.findAll();
    }
}