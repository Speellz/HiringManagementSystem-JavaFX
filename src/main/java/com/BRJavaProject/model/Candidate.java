package com.BRJavaProject.model;

public class Candidate {
    private int id;
    private String name;
    private String surname;
    private String role;
    private int companyId;
    private int positionId;
    private int userId;
    private String addedByName;
    private String assignedProject;

    // Constructor
    public Candidate(int id, String name, String surname, String role, int companyId, int positionId, int userId) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.companyId = companyId;
        this.positionId = positionId;
        this.userId = userId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    private String companyName;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddedByName() {
        return addedByName;
    }

    public void setAddedByName(String addedByName) {
        this.addedByName = addedByName;
    }

    public String getAssignedProject() {
        return assignedProject;
    }

    public void setAssignedProject(String assignedProject) {
        this.assignedProject = assignedProject;
    }
}
