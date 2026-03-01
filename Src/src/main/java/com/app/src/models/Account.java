package com.app.src.models;

public class Account {
    private String accountId;
    private String userId;
    private String userName;
    private String password;

    public Account() {  }

    public Account(String accountId, String userId, String userName, String password) {
        this.accountId = accountId;
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
