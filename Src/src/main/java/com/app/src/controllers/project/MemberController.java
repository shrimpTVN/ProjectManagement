package com.app.src.controllers.project;

import com.app.src.models.Project;

public class MemberController implements IProjectDetailSubView {
    @Override
    public void renderData(Project project, String adminName) {
        System.out.println(project.getProjectName() + " " + adminName);
    }
}
