package com.app.src.models;

import java.util.ArrayList;
import java.util.Date;

public class Project {
    private int projectId;
    private String projectName;
    private String projectDescription;
    private Date projectStartDate;
    private Date projectEndDate;
    private ArrayList<Task> tasks;
    private ArrayList<ProjectJoining> joinings;
    private int userRoleId; // role of the currently logged-in user in this project

    public Project() {
    }

    public Project(int projectId, String projectName, String projectDescription, Date projectStartDate, Date projectEndDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public ArrayList<ProjectJoining> getJoinings() {
        return joinings;
    }

    public void setJoinings(ArrayList<ProjectJoining> joinings) {
        this.joinings = joinings;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Date getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(Date projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public Date getProjectEndDate() {
        return projectEndDate;
    }

    public void setProjectEndDate(Date projectEndDate) {
        this.projectEndDate = projectEndDate;
    }

    public int getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(int userRoleId) {
        this.userRoleId = userRoleId;
    }
}
