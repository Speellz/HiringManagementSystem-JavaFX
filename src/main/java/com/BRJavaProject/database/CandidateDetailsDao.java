package com.BRJavaProject.database;

import com.BRJavaProject.model.CandidateDetails;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CandidateDetailsDao {

    private final Connection connection;

    public CandidateDetailsDao() {
        this.connection = DatabaseConnection.getConnection();
    }


    public CandidateDetails getCandidateDetailsById(int candidateId) {
        String sql = "SELECT DetailID, CandidateID, Status, StatusExplanation, Notes, CurrentCompany " +
                "FROM CandidateDetails WHERE CandidateID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, candidateId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new CandidateDetails(
                            rs.getInt("DetailID"),
                            rs.getInt("CandidateID"),
                            rs.getString("Status"),
                            rs.getString("StatusExplanation"),
                            rs.getString("Notes"),
                            rs.getString("CurrentCompany")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching candidate details: " + e.getMessage());
        }
        return null;
    }

    public void addCandidateDetails(CandidateDetails details) {
        String sql = "INSERT INTO CandidateDetails (CandidateID, Status, StatusExplanation, Notes, CurrentCompany) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, details.getCandidateId());
            pstmt.setString(2, details.getStatus());
            pstmt.setString(3, details.getStatusExplanation());
            pstmt.setString(4, details.getNotes());
            pstmt.setString(5, details.getCurrentCompany());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding candidate details: " + e.getMessage());
        }
    }

    public boolean updateCandidateDetails(CandidateDetails details) {
        String sql = "UPDATE CandidateDetails SET Status = ?, StatusExplanation = ?, Notes = ?, CurrentCompany = ? WHERE CandidateID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, details.getStatus());
            pstmt.setString(2, details.getStatusExplanation());
            pstmt.setString(3, details.getNotes());
            pstmt.setString(4, details.getCurrentCompany());
            pstmt.setInt(5, details.getCandidateId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating candidate details: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteCandidateDetails(int candidateId) {
        String sql = "DELETE FROM CandidateDetails WHERE CandidateID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, candidateId);
            pstmt.executeUpdate();
            System.out.println("Candidate details deleted successfully.");
        } catch (SQLException e) {
            System.err.println("Error deleting candidate details: " + e.getMessage());
        }
        return false;
    }
}
