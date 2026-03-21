package com.server.model;

import java.util.Date;

public class ChatMessage {
    private int userId;
    private int taskId;

    private String commemt;
    private Date date;

    public ChatMessage(){}
    public ChatMessage(int userId, int taskId, String commemt, Date date) {
        this.userId = userId;
        this.taskId = taskId;
        this.commemt = commemt;
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getCommemt() {
        return commemt;
    }

    public void setCommemt(String commemt) {
        this.commemt = commemt;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}