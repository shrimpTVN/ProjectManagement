package com.app.src.models;
import java.sql.Timestamp;

public class Project {
    private String proId;
    private String proName;
    private Timestamp proStartDate;
    private Timestamp proEndDate;
    private String proDescription;

    public Project() {}

    public Project(String proId, String proName, Timestamp proStartDate, Timestamp proEndDate, String proDescription) {
        this.proId = proId;
        this.proName = proName;
        this.proStartDate = proStartDate;
        this.proEndDate = proEndDate;
        this.proDescription = proDescription;
    }

    // Getters and Setters
    public String getProId() { return proId; }
    public void setProId(String proId) { this.proId = proId; }

    public String getProName() { return proName; }
    public void setProName(String proName) { this.proName = proName; }

    public Timestamp getProStartDate() { return proStartDate; }
    public void setProStartDate(Timestamp proStartDate) { this.proStartDate = proStartDate; }

    public Timestamp getProEndDate() { return proEndDate; }
    public void setProEndDate(Timestamp proEndDate) { this.proEndDate = proEndDate; }

    public String getProDescription() { return proDescription; }
    public void setProDescription(String proDescription) { this.proDescription = proDescription; }
}