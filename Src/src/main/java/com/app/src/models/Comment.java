package com.app.src.models;

import java.util.Date;

public class Comment {
    private String id;
    private String taskId;
    private String comment;
    private Date date;
    private String userId;
    private String previousCommentId;

    public Comment(){}
    public Comment(String id, String taskId, String comment, Date date, String userId, String previousCommentId) {
        this.id = id;
        this.taskId = taskId;
        this.comment = comment;
        this.date = date;
        this.userId = userId;
        this.previousCommentId = previousCommentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPreviousCommentId() {
        return previousCommentId;
    }

    public void setPreviousCommentId(String previousCommentId) {
        this.previousCommentId = previousCommentId;
    }
}
