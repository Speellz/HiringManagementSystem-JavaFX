package com.BRJavaProject.database;

import com.BRJavaProject.model.Skill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDao {

    private final Connection connection;

    public SkillDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    public List<Skill> getSkillsByUserId(int userId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM Skills WHERE UserID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Skill skill = new Skill();
                    skill.setSkillId(rs.getInt("SkillID"));
                    skill.setUserId(rs.getInt("UserID"));
                    skill.setSkillName(rs.getString("SkillName"));
                    skill.setProficiencyLevel(rs.getString("ProficiencyLevel"));
                    skill.setCertification(rs.getString("Certification"));
                    skills.add(skill);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching skills: " + e.getMessage());
        }
        return skills;
    }

    public boolean addSkill(Skill skill) {
        String sql = "INSERT INTO Skills (UserID, SkillName, ProficiencyLevel, Certification) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, skill.getUserId());
            pstmt.setString(2, skill.getSkillName());
            pstmt.setString(3, skill.getProficiencyLevel());
            pstmt.setString(4, skill.getCertification());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding skill: " + e.getMessage());
        }
        return false;
    }

    public boolean updateSkill(Skill skill) {
        String sql = "UPDATE Skills SET SkillName = ?, ProficiencyLevel = ?, Certification = ? WHERE SkillID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, skill.getSkillName());
            pstmt.setString(2, skill.getProficiencyLevel());
            pstmt.setString(3, skill.getCertification());
            pstmt.setInt(4, skill.getSkillId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating skill: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteSkill(int skillId) {
        String sql = "DELETE FROM Skills WHERE SkillID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, skillId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting skill: " + e.getMessage());
        }
        return false;
    }
}
