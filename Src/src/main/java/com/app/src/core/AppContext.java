package com.app.src.core;

import com.app.src.core.session.UserSession;
import com.app.src.models.User;

public class AppContext {

    private static AppContext instance;
    private UserSession userSession;

    private AppContext(){
        userSession = UserSession.getInstance();
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

}
