package com.app.src.models;

import java.util.ArrayList;
import java.util.Date;

public class Task {
    private int taskId;
    private String taskName;
    private String taskDescription;
    private String taskStartTime;
    private String taskEndTime;
    private User user;
//    private ArrayList<StatusUpdating> statusUpdatingList;



    public Task(){}

    public Task(int taskId, String taskName, String taskDescription, String taskStartTime, String taskEndTime) {
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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
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

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public String getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(String taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getTaskStatus() {
        return "Task_status";
    }
}
