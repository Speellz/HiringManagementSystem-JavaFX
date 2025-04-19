package com.BRJavaProject.database;

import com.BRJavaProject.model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyDao {
    public void addCompany(Company company) {
        String sql = "INSERT INTO Company (CompanyName, Date, UserID) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, company.getCompanyName());
            pstmt.setDate(2, company.getDate());
            pstmt.setInt(3, company.getUserId());
            pstmt.executeUpdate();
            System.out.println("Company successfully added.");
        } catch (SQLException e) {
            System.err.println("Error adding company: " + e.getMessage());
        }
    }

    public void updateCompany(Company company) {
        String sql = "UPDATE Company SET CompanyName = ?, Date = ? WHERE CompanyID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, company.getCompanyName());
            pstmt.setDate(2, company.getDate());
            pstmt.setInt(3, company.getCompanyId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating company: " + e.getMessage());
        }
    }

    public void deleteCompany(int companyId) {
        String sql = "DELETE FROM Company WHERE CompanyID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting company: " + e.getMessage());
        }
    }

    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM Company";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Company company = new Company(
                        rs.getInt("CompanyID"),
                        rs.getString("CompanyName"),
                        rs.getDate("Date"),
                        rs.getInt("UserID")
                );
                companies.add(company);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving companies: " + e.getMessage());
        }
        return companies;
    }

    public List<String> getCompaniesInProjects() {
        List<String> companies = new ArrayList<>();
        String sql = "SELECT DISTINCT c.CompanyName " +
                "FROM Company c " +
                "INNER JOIN Projects p ON c.CompanyID = p.CompanyID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                companies.add(rs.getString("CompanyName"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching companies in projects: " + e.getMessage());
        }

        return companies;
    }

    public Company getCompanyById(int companyId) {
        String sql = "SELECT * FROM Company WHERE CompanyID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, companyId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Company(
                        rs.getInt("CompanyID"),
                        rs.getString("CompanyName"),
                        rs.getDate("Date"),
                        rs.getObject("UserID") != null ? rs.getInt("UserID") : -1
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching company: " + e.getMessage());
        }
        return null;
    }

}
