package com.app.src.models;

import java.util.Date;

public class Comment {

    private int taskId;
    private String comment;
    private Date date;
    private int userId;


    public Comment(){}
    public Comment( int taskId, int userId, String comment, Date date) {
        this.taskId = taskId;
        this.comment = comment;
        this.date = date;
        this.userId = userId;

    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
