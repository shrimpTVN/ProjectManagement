package com.app.src.models;

import java.util.Date;

public class Task {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private Date taskStartTime;
    private Date taskEndTime;
    private String projectId;
    private String userId;

    public Task(){}

    public Task(String taskId, String taskName, String taskDescription, Date taskStartTime, Date taskEndTime, String userId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStartTime = taskStartTime;
        this.taskEndTime = taskEndTime;
        this.userId = userId;
        this.projectId = projectId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Date getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Date taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Date getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Date taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
