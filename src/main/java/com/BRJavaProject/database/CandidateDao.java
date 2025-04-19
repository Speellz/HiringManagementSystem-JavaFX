package com.BRJavaProject.database;

import com.BRJavaProject.model.Candidate;
import com.BRJavaProject.model.CandidateDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDao {

    private final Connection connection;

    public CandidateDao() {
        connection = DatabaseConnection.getConnection();
    }

    public int addCandidate(Candidate candidate) {
        String sql = "INSERT INTO Candidates (Name, Surname, Role, CompanyID, PositionID, UserID) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, candidate.getName());
            pstmt.setString(2, candidate.getSurname());
            pstmt.setString(3, candidate.getRole());
            pstmt.setInt(4, candidate.getCompanyId());
            pstmt.setInt(5, candidate.getPositionId());
            pstmt.setInt(6, candidate.getUserId());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error adding candidate: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateCandidate(Candidate candidate) {
        String sql = "UPDATE Candidates SET Name = ?, Surname = ?, Role = ?, CompanyID = ?, PositionID = ? WHERE CandidateID = ?";
        try (var conn = DatabaseConnection.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, candidate.getName());
            pstmt.setString(2, candidate.getSurname());
            pstmt.setString(3, candidate.getRole());
            pstmt.setInt(4, candidate.getCompanyId());
            pstmt.setInt(5, candidate.getPositionId());
            pstmt.setInt(6, candidate.getId());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error updating candidate: " + e.getMessage());
        }
        return false;
    }

    public void deleteCandidate(int candidateId) {
        try {
            String deleteDetailsQuery = "DELETE FROM CandidateDetails WHERE CandidateID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteDetailsQuery)) {
                stmt.setInt(1, candidateId);
                stmt.executeUpdate();
            }

            String deleteCandidateQuery = "DELETE FROM Candidates WHERE CandidateID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteCandidateQuery)) {
                stmt.setInt(1, candidateId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error deleting candidate: " + e.getMessage());
        }
    }

    public void addCandidateDetails(CandidateDetails details) {
        String sql = "INSERT INTO CandidateDetails (CandidateID, Status, StatusExplanation, Notes) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, details.getCandidateId());
            pstmt.setString(2, details.getStatus());
            pstmt.setString(3, details.getStatusExplanation());
            pstmt.setString(4, details.getNotes());
            pstmt.executeUpdate();
            System.out.println("Candidate details added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding candidate details: " + e.getMessage());
        }
    }

    public boolean deleteCandidateDetails(int candidateId) {
        String sql = "DELETE FROM CandidateDetails WHERE CandidateID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, candidateId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting candidate details: " + e.getMessage());
        }
        return false;
    }

    public List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        String sql = "SELECT c.CandidateID, c.Name, c.Surname, c.Role, c.CompanyID, c.PositionID, c.UserID, " +
                "u.Name AS AddedByName, p.ProjectName AS AssignedProject " +
                "FROM Candidates c " +
                "LEFT JOIN Users u ON c.UserID = u.UserID " +
                "LEFT JOIN ProjectPositions pp ON c.PositionID = pp.PositionID " +
                "LEFT JOIN Projects p ON pp.ProjectID = p.Id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Candidate candidate = new Candidate(
                        rs.getInt("CandidateID"),
                        rs.getString("Name"),
                        rs.getString("Surname"),
                        rs.getString("Role"),
                        rs.getInt("CompanyID"),
                        rs.getInt("PositionID"),
                        rs.getInt("UserID")
                );
                candidate.setAddedByName(rs.getString("AddedByName"));
                candidate.setAssignedProject(rs.getString("AssignedProject"));
                candidates.add(candidate);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving candidates: " + e.getMessage());
        }
        return candidates;
    }

    public List<String> getPositionsByCompany(int companyId) {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT PositionName FROM ProjectPositions WHERE ProjectID IN " +
                "(SELECT Id FROM Projects WHERE CompanyID = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String positionName = rs.getString("PositionName");
                if (positionName != null && !positionName.trim().isEmpty()) {
                    positions.add(positionName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    public List<String> getPositionsByProject(int projectId) {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT PositionName FROM ProjectPositions WHERE ProjectID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    positions.add(rs.getString("PositionName"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching positions for project: " + e.getMessage());
        }

        return positions;
    }

    public int getDefaultPositionByProject(int projectId) {
        String sql = "SELECT TOP 1 PositionID FROM ProjectPositions WHERE ProjectID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("PositionID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching default position for project: " + e.getMessage());
        }
        return -1;
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

    public int getAssignedCandidatesCount(int projectId) {
        String sql = "SELECT COUNT(*) AS AssignedCount FROM Candidates WHERE PositionID IN " +
                "(SELECT PositionID FROM ProjectPositions WHERE ProjectID = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("AssignedCount");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching assigned candidates count: " + e.getMessage());
        }
        return 0;
    }

    public int getPositionIdByName(String positionName, int companyId) {
        String sql = "SELECT PositionID FROM ProjectPositions WHERE PositionName = ? AND ProjectID IN (SELECT Id FROM Projects WHERE CompanyID = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, positionName);
            pstmt.setInt(2, companyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("PositionID");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching position ID: " + e.getMessage());
        }
        return -1;
    }
}
