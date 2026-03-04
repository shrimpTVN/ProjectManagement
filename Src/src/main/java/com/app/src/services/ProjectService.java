package com.app.src.services;

import com.app.src.daos.ProjectDAO;
import com.app.src.models.Project;

import java.util.ArrayList;

public class ProjectService {
    private static ProjectDAO projectDAO;
    public ProjectService() {
        projectDAO = ProjectDAO.getInstance();
    }
    public static ArrayList<Project> getAllProjects(int userId) {

        return projectDAO.getInstance().findByUserId(userId);
    }

}
