package com.app.src.models;

import java.util.Date;

public class Notification {

    private  String notiTitle;
    private String notiDescription;
    private boolean notiIsRead;
    private String notiTime;
    private int userId;

    public Notification(){}

    public Notification(String notiTitle, String notiDescription, boolean notiIsRead, String notiTime,  int userId) {

        this.notiTitle = notiTitle;
        this.notiDescription = notiDescription;
        this.notiIsRead = notiIsRead;
        this.notiTime = notiTime;
        this.userId = userId;

    }
    public Notification(String notiTitle, String notiDescription, int userId) {

        this.notiTitle = notiTitle;
        this.notiDescription = notiDescription;
        this.notiIsRead = false;
        this.notiTime = String.valueOf(new Date());
        this.userId = userId;

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNotiTitle () {
        return notiTitle;
    }

    public void setNotiTitle( String notiTitle) {
        this.notiTitle = notiTitle;
    }

    public String getNotiDescription() {
        return notiDescription;
    }

    public void setNotiDescription(String notiDescription) {
        this.notiDescription = notiDescription;
    }

    public boolean isNotiIsRead() {
        return notiIsRead;
    }

    public void setNotiIsRead(boolean notiIsRead) {
        this.notiIsRead = notiIsRead;
    }

    public String getNotiTime() { return notiTime;}
    public void  setNotiTime ( String Time ) {
        this.notiTime = Time;
    }
}
