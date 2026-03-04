package com.app.src.models;

import java.io.Serializable;
import java.util.ArrayList;
//User se luu lai thong tin tai khoan, nhung task va project cua user do
public class User {
    private int userId;
    private String userName;
    private String userDoB;
    private boolean userGender;
    private String userPhoneNumber;
    private Account account;



    public User(){
        this.userId = -1;
    }
    public User(int userId, String userName, String userDoB, boolean userGender, String userPhoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.userDoB = userDoB;
        this.userGender = userGender;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
