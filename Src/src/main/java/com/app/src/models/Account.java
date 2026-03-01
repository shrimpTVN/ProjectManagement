package com.app.src.models;

public class Account {
    private String accUserName;
    private String accPassword;
    private String userId;

    // Constructor mặc định
    public Account() {
    }

    // Constructor đầy đủ tham số
    public Account(String accUserName, String accPassword, String userId) {
        this.accUserName = accUserName;
        this.accPassword = accPassword;
        this.userId = userId;
    }

    // Getters và Setters
    public String getAccUserName() { return accUserName; }
    public void setAccUserName(String accUserName) { this.accUserName = accUserName; }

    public String getAccPassword() { return accPassword; }
    public void setAccPassword(String accPassword) { this.accPassword = accPassword; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}