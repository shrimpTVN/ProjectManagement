package com.app.src.services;

import com.app.src.daos.ProjectJoiningDAO;

public class ProjectJoiningService {
    private static ProjectJoiningDAO projectJoiningDao;
    public ProjectJoiningService() {
        projectJoiningDao = ProjectJoiningDAO.getInstance();
    }
    public String getAdmin(int projectId)
    {
        return projectJoiningDao.getAdmin(projectId);
    }

}
