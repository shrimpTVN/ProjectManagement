package com.app.src.core.session;

import com.app.src.models.User;

public class UserSession {
    private static UserSession instance;
    private User user;

    private UserSession(){
        user = new User();
    }

    public static UserSession getInstance(){
        if(instance == null){
            instance = new UserSession();
        }

        return instance;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
