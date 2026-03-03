package com.app.src.models;

public class Notification {
    private String notiId;
    private String notiDescription;
    private boolean notiIsRead;

    public Notification(){}

    public Notification(String notiId, String notiDescription, boolean notiIsRead) {
        this.notiId = notiId;
        this.notiDescription = notiDescription;
        this.notiIsRead = notiIsRead;
    }

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
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
