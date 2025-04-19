package com.BRJavaProject.model;

import java.sql.Date;

public class Company {
    private int companyId;
    private String companyName;
    private Date date;
    private int userId;

    // Add company
    public Company() {
    }

    // Constructor
    public Company(int companyId, String companyName, Date date, int userId) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.date = date;
        this.userId = userId;
    }

    // Getters and Setters
    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
