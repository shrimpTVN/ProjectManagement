package com.app.src.models;
import java.sql.Timestamp;

public class Task {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private Timestamp taskStartDate;
    private Timestamp taskDeadline;
    private String proId;

    public Task() {}

    public Task(String taskId, String taskName, String taskDescription, Timestamp taskStartDate, Timestamp taskDeadline, String proId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStartDate = taskStartDate;
        this.taskDeadline = taskDeadline;
        this.proId = proId;
    }

    // Getters and Setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public Timestamp getTaskStartDate() { return taskStartDate; }
    public void setTaskStartDate(Timestamp taskStartDate) { this.taskStartDate = taskStartDate; }

    public Timestamp getTaskDeadline() { return taskDeadline; }
    public void setTaskDeadline(Timestamp taskDeadline) { this.taskDeadline = taskDeadline; }

    public String getProId() { return proId; }
    public void setProId(String proId) { this.proId = proId; }
}