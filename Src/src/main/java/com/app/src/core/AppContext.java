package com.app.src.core;

import com.app.src.core.session.UserSession;
import com.app.src.models.Project;
import com.app.src.models.User;
import com.app.src.services.ProjectService;

import java.util.ArrayList;

public class AppContext {

    private static AppContext instance;
    private final UserSession userSession;
    private static ArrayList<Project> projects;


    private AppContext(){
        userSession = UserSession.getInstance();
        ProjectService projectService = new ProjectService();
       projects =  ProjectService.getAllProjects(userSession.getUser().getUserId());
//       projects = ProjectService.getAllProjects(3);
    }


    public static AppContext getInstance(){
        if (instance == null){
            instance = new AppContext();
        }

        return instance;
    }

    public User getUserData(){
        return userSession.getUser();
    }

    public static ArrayList<Project> getProjects(){
        return projects;
    }
}
