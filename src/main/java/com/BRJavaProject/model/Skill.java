package com.BRJavaProject.model;

public class Skill {
    private int skillId;
    private int userId;
    private String skillName;
    private String proficiencyLevel;
    private String certification;

    // Default constructor
    public Skill() {
    }

    // Constructor for user input
    public Skill(String skillName, String proficiencyLevel, String certification) {
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.certification = certification;
    }

    // Constructor including userId
    public Skill(int userId, String skillName, String proficiencyLevel, String certification) {
        this.userId = userId;
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
        this.certification = certification;
    }

    // Getters and Setters
    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    // Override toString() for better debugging or displaying skill details
    @Override
    public String toString() {
        return "Skill{" +
                "skillId=" + skillId +
                ", userId=" + userId +
                ", skillName='" + skillName + '\'' +
                ", proficiencyLevel='" + proficiencyLevel + '\'' +
                ", certification='" + certification + '\'' +
                '}';
    }
}
