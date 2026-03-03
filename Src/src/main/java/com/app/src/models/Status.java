package com.app.src.models;

public class Status {
    private String staId;
    private String staName;

    public Status() {}

    public Status(String staId, String staName) {
        this.staId = staId;
        this.staName = staName;
    }

    // Getters and Setters
    public String getStaId() { return staId; }
    public void setStaId(String staId) { this.staId = staId; }

    public String getStaName() { return staName; }
    public void setStaName(String staName) { this.staName = staName; }
}