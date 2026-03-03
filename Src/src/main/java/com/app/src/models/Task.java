package com.app.src.models;

import java.util.ArrayList;
import java.util.Date;

public class Task {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private Date taskStartTime;
    private Date taskEndTime;
    private User user;
//    private ArrayList<StatusUpdating> statusUpdatingList;



    public Task(){}

    public Task(String taskId, String taskName, String taskDescription, Date taskStartTime, Date taskEndTime) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStartTime = taskStartTime;
        this.taskEndTime = taskEndTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

}
