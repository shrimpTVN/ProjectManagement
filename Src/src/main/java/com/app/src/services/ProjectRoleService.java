package com.app.src.services;

import com.app.src.daos.ProjectRoleDAO;
import com.app.src.models.ProjectRole;

import java.util.List;

public class ProjectRoleService {
    private static ProjectRoleDAO projectRoleDao ;

    public ProjectRoleService() {
        projectRoleDao = ProjectRoleDAO.getInstance();
    }

    public static List<ProjectRole> getAllRoles() {
        return projectRoleDao.findAll();
    }

    public static String getRoleNameById(int roleId){
        return projectRoleDao.findById(roleId).getRoleName();
    }
}