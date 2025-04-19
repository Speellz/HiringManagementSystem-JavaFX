package com.BRJavaProject.database;

import com.BRJavaProject.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDao {

    private final Connection connection;

    public ProjectDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    public int addProject(Project project) {
        String sql = "INSERT INTO Projects (ProjectName, CompanyID, ProjectDate) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, project.getProjectName());
            pstmt.setInt(2, project.getCompanyId());
            pstmt.setDate(3, project.getProjectDate());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding project: " + e.getMessage());
        }
        return -1;
    }

    public boolean updateProject(Project project) {
        String updateProjectsQuery = "UPDATE Projects " +
                "SET ProjectName = ?, " +
                "    CompanyId = ?, " +
                "    ProjectDate = ? " +
                "WHERE Id = ?";

        String updateProjectPositionsQuery = "UPDATE ProjectPositions " +
                "SET PositionName = ?, " +
                "    CandidateCount = ? " +
                "WHERE ProjectID = ?";

        try (PreparedStatement projectStmt = connection.prepareStatement(updateProjectsQuery);
             PreparedStatement positionStmt = connection.prepareStatement(updateProjectPositionsQuery)) {

            // Projects tablosunu güncelle
            projectStmt.setString(1, project.getProjectName());
            projectStmt.setInt(2, project.getCompanyId());
            projectStmt.setDate(3, project.getProjectDate());
            projectStmt.setInt(4, project.getId());
            projectStmt.executeUpdate();

            // ProjectPositions tablosunu güncelle
            positionStmt.setString(1, project.getPositionName());
            positionStmt.setInt(2, project.getCandidateCount());
            positionStmt.setInt(3, project.getId());
            positionStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("Error updating project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProject(int projectId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String deletePositionsSQL = "DELETE FROM ProjectPositions WHERE ProjectID = ?";
            try (PreparedStatement deletePositionsStmt = connection.prepareStatement(deletePositionsSQL)) {
                deletePositionsStmt.setInt(1, projectId);
                deletePositionsStmt.executeUpdate();
            }

            String deleteProjectSQL = "DELETE FROM Projects WHERE Id = ?";
            try (PreparedStatement deleteProjectStmt = connection.prepareStatement(deleteProjectSQL)) {
                deleteProjectStmt.setInt(1, projectId);
                int rowsAffected = deleteProjectStmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addPositionToProject(int projectId, String positionName, int candidateCount) {
        String sql = "INSERT INTO ProjectPositions (ProjectID, PositionName, CandidateCount) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setString(2, positionName);
            pstmt.setInt(3, candidateCount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding position to project: " + e.getMessage());
        }
    }

    public void updateProjectPosition(int positionId, String positionName, int candidateCount) {
        String sql = "UPDATE ProjectPositions SET PositionName = ?, CandidateCount = ? WHERE PositionID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, positionName);
            pstmt.setInt(2, candidateCount);
            pstmt.setInt(3, positionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating position: " + e.getMessage());
        }
    }

    public void deleteProjectPosition(int positionId) {
        String sql = "DELETE FROM ProjectPositions WHERE PositionID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, positionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting position: " + e.getMessage());
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.Id, p.ProjectName, p.ProjectDate, c.CompanyName, " +
                "COALESCE(pp.PositionName, 'No Position Available') AS PositionName, pp.CandidateCount " +
                "FROM Projects p " +
                "INNER JOIN Company c ON p.CompanyID = c.CompanyID " +
                "LEFT JOIN ProjectPositions pp ON p.Id = pp.ProjectID";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Project project = new Project();
                project.setId(rs.getInt("Id"));
                project.setProjectName(rs.getString("ProjectName"));
                project.setCompanyName(rs.getString("CompanyName"));
                project.setProjectDate(rs.getDate("ProjectDate"));
                project.setPositionName(rs.getString("PositionName"));
                project.setCandidateCount(rs.getInt("CandidateCount"));

                int assignedCandidates = getAssignedCandidatesCount(project.getId());
                project.setAssignedCandidates(assignedCandidates);
                project.setRemainingCandidates(project.getCandidateCount() - assignedCandidates);

                projects.add(project);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching projects: " + e.getMessage());
        }
        return projects;
    }

    public int getCompanyIdByProjectId(int projectId) {
        String sql = "SELECT CompanyID FROM Projects WHERE Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CompanyID");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching CompanyID: " + e.getMessage());
        }
        return -1;
    }

    public List<String> getAllProjectNames() {
        List<String> projectNames = new ArrayList<>();
        String sql = "SELECT ProjectName FROM Projects";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                projectNames.add(rs.getString("ProjectName"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching project names: " + e.getMessage());
        }
        return projectNames;
    }

    public String getProjectNameById(int positionId) {
        String sql = "SELECT p.ProjectName " +
                "FROM Projects p " +
                "JOIN ProjectPositions pp ON p.Id = pp.ProjectID " +
                "WHERE pp.PositionID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, positionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("ProjectName");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching project name by ID: " + e.getMessage());
        }
        return null;
    }

    public int getProjectIdByName(String projectName) {
        String sql = "SELECT Id FROM Projects WHERE ProjectName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching Project ID by name: " + e.getMessage());
        }
        return -1;
    }

    public Map<String, Integer> getCompanyMappings() {
        Map<String, Integer> companyMappings = new HashMap<>();
        String sql = "SELECT ProjectName, CompanyID FROM Projects";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                companyMappings.put(rs.getString("ProjectName"), rs.getInt("CompanyID"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching company mappings: " + e.getMessage());
        }
        return companyMappings;
    }

    public Map<String, Integer> getPositionMappings() {
        Map<String, Integer> positionMappings = new HashMap<>();
        String sql = "SELECT ProjectName, Id AS PositionID FROM Projects";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                positionMappings.put(rs.getString("ProjectName"), rs.getInt("PositionID"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching position mappings: " + e.getMessage());
        }
        return positionMappings;
    }

    public String getPositionNamesByProjectId(int projectId) {
        String sql = "SELECT PositionName FROM ProjectPositions WHERE ProjectID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("PositionName");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching position name: " + e.getMessage());
        }
        return null;
    }

    public String getCompanyNameById(int companyId) {
        String query = "SELECT CompanyName FROM Company WHERE CompanyID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("CompanyName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getUserIdByName(String userName) {
        String query = "SELECT UserID FROM Users WHERE Username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("UserID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getUserNameById(int userId) {
        String query = "SELECT Username FROM Users WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getCompanyIdByName(String companyName) {
        String query = "SELECT CompanyID FROM Company WHERE CompanyName = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, companyName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("CompanyID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int addProjectAndGetId(Project project) {
        int projectId = -1;
        String sql = "INSERT INTO Projects (ProjectName, CompanyID, ProjectDate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, project.getProjectName());
            stmt.setInt(2, project.getCompanyId());
            stmt.setDate(3, project.getProjectDate());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    projectId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectId;
    }

    private int getAssignedCandidatesCount(int projectId) {
        String sql = "SELECT COUNT(*) AS AssignedCount FROM Candidates WHERE PositionID IN " +
                "(SELECT PositionID FROM ProjectPositions WHERE ProjectID = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AssignedCount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching assigned candidates count: " + e.getMessage());
        }
        return 0;
    }
}
