package com.BRJavaProject.model;

import java.sql.Date;

public class Project {
    private int id;
    private String projectName;
    private String companyName;
    private int companyId;
    private Date projectDate;
    private String positionName;
    private int candidateCount;
    private int assignedCandidates;
    private int remainingCandidates;
    private String description;
    private int userId;

    public Project() {
    }

    public Project(int id, String projectName, int companyId, Date projectDate, String positionName, int candidateCount) {
        this.id = id;
        this.projectName = projectName;
        this.companyId = companyId;
        this.projectDate = projectDate;
        this.positionName = positionName;
        this.candidateCount = candidateCount;
        this.assignedCandidates = 0;
        this.remainingCandidates = candidateCount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public Date getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(Date projectDate) {
        this.projectDate = projectDate;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public int getCandidateCount() {
        return candidateCount;
    }

    public void setCandidateCount(int candidateCount) {
        this.candidateCount = candidateCount;
    }

    public int getAssignedCandidates() {
        return assignedCandidates;
    }

    public void setAssignedCandidates(int assignedCandidates) {
        this.assignedCandidates = assignedCandidates;
    }

    public int getRemainingCandidates() {
        return remainingCandidates;
    }

    public void setRemainingCandidates(int remainingCandidates) {
        this.remainingCandidates = remainingCandidates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
