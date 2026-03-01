package com.app.src.models;

public class User {
    private String userId;
    private String userName;
    private String dateOfBirth;
    private String sex;
    private int phoneNumber;

    // Constructor mặc định
    public User() {
    }

    // Constructor đầy đủ tham số
    public User(String userId, String userName, String dateOfBirth, String sex, int phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.phoneNumber = phoneNumber;
    }

    // Getters và Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public int getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }
}