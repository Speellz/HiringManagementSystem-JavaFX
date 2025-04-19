package com.BRJavaProject.model;

public class CandidateDetails {
    private int detailId;
    private int candidateId;
    private String status;
    private String statusExplanation;
    private String notes;
    private String currentCompany;

    //5 constructor
    public CandidateDetails(int detailId, int candidateId, String status, String statusExplanation, String notes) {
        this.detailId = detailId;
        this.candidateId = candidateId;
        this.status = status;
        this.statusExplanation = statusExplanation;
        this.notes = notes;
    }

    // 6 constructor
    public CandidateDetails(int detailId, int candidateId, String status, String statusExplanation, String notes, String currentCompany) {
        this.detailId = detailId;
        this.candidateId = candidateId;
        this.status = status;
        this.statusExplanation = statusExplanation;
        this.notes = notes;
        this.currentCompany = currentCompany;
    }

    // Getters and Setters
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusExplanation() {
        return statusExplanation;
    }

    public void setStatusExplanation(String statusExplanation) {
        this.statusExplanation = statusExplanation;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }
}
