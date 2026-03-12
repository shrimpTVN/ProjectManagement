package com.app.src.dtos;

import com.app.src.models.Task;

public class PersonalTaskDTO extends Task {
    private String projectName;
    private String statusName;

    public PersonalTaskDTO() {}

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
}
