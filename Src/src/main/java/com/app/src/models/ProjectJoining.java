package com.app.src.models;

import javax.management.relation.Role;
import java.util.Date;
//Project joining se luu lai user tham gia du an thoi gian nao, voi role la gi
public class ProjectJoining {

    private Date joinDate;
    private Role role;
    private User user;

    public ProjectJoining(){}
    public ProjectJoining( Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
