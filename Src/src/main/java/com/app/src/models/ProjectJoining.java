package com.app.src.models;

import java.util.Date;

public class ProjectJoining {
    private String projectId;
    private String roleId;
    private String userId;
    private Date joinDate;

    public ProjectJoining(){}
    public ProjectJoining(String projectId, String roleId, String userId, Date joinDate) {
        this.projectId = projectId;
        this.roleId = roleId;
        this.userId = userId;
        this.joinDate = joinDate;

    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }
}
