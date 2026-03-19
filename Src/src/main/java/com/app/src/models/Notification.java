package com.app.src.models;

public class Notification {
    private int notiId;
    private  String notiTitle;
    private String notiDescription;
    private boolean notiIsRead;
    private String notiTime;

    public Notification(){}

    public Notification(int notiId, String notiTitle, String notiDescription, boolean notiIsRead, String notiTime) {
        this.notiId = notiId;
        this.notiTitle = notiTitle;
        this.notiDescription = notiDescription;
        this.notiIsRead = notiIsRead;
        this.notiTime = notiTime;
    }

    public int getNotiId() {
        return notiId;
    }

    public void setNotiId(int notiId) {
        this.notiId = notiId;
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
