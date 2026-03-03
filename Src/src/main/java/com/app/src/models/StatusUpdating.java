package com.app.src.models;

import java.util.Date;

public class StatusUpdating {
    private Date date;
    private String content;
    private TaskStatus taskStatus;

    public StatusUpdating() {}

    public StatusUpdating(final Date date, final String content) {
        this.date = date;
        this.content = content;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
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

}
