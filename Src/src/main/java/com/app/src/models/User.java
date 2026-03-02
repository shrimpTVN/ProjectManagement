package com.app.src.models;

import java.io.Serializable;

public class User {
    private String userId;
    private String userName;
    private String userDoB;
    private boolean userGender;
    private String userPhoneNumber;

    public User(){}
    public User(String userId, String userName, String userDoB, boolean userGender, String userPhoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.userDoB = userDoB;
        this.userGender = userGender;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDoB() {
        return userDoB;
    }

    public void setUserDoB(String userDoB) {
        this.userDoB = userDoB;
    }

    public boolean isUserGender() {
        return userGender;
    }

    public void setUserGender(boolean userGender) {
        this.userGender = userGender;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}
