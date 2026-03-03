package com.app.src.models;

public class Notification {
    private int notiId;
    private String notiDescription;
    private boolean notiIsRead;

    public Notification(){}

    public Notification(int notiId, String notiDescription, boolean notiIsRead) {
        this.notiId = notiId;
        this.notiDescription = notiDescription;
        this.notiIsRead = notiIsRead;
    }

    public int getNotiId() {
        return notiId;
    }

    public void setNotiId(int notiId) {
        this.notiId = notiId;
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
}
