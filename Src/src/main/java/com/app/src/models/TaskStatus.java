package com.app.src.models;

public class TaskStatus {
    private String statusId;
    private String statusName;
    private String statusDescription;

    public TaskStatus(){}
    public TaskStatus(String statusId, String statusName, String statusDescription) {
        this.statusId = statusId;
        this.statusName = statusName;
        this.statusDescription = statusDescription;

    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }
}
