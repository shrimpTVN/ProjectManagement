package com.app.src.models;

import java.util.Date;

public class StatusUpdating {
    private Date date;
    private String content;
    private String statusId;
    private String taskId;

    public StatusUpdating() {}

    public StatusUpdating(final Date date, final String content, final String statusId, final String taskId) {
        this.date = date;
        this.content = content;
        this.statusId = statusId;
        this.taskId = taskId;

    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
