package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CandidateDao;
import com.BRJavaProject.database.ProjectDao;
import com.BRJavaProject.database.DatabaseConnection;
import com.BRJavaProject.model.Candidate;
import com.BRJavaProject.model.Project;
import com.BRJavaProject.ui.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCandidateController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField roleField;

    @FXML
    private ComboBox<String> projectComboBox;

    private final CandidateDao candidateDao = new CandidateDao();
    private final ProjectDao projectDao = new ProjectDao();
    private final Map<String, Integer> projectMap = new HashMap<>();

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void initialize() {
        loadProjectData();
    }

    private void loadProjectData() {
        List<Project> projects = projectDao.getAllProjects();
        for (Project project : projects) {
            projectMap.put(project.getProjectName(), project.getId());
        }
        projectComboBox.setItems(FXCollections.observableArrayList(projectMap.keySet()));
    }

    @FXML
    private void handleAddCandidate() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String role = roleField.getText();
        String projectName = projectComboBox.getValue();

        if (name.isEmpty() || surname.isEmpty() || role.isEmpty() || projectName == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields!");
            return;
        }

        int projectId = projectMap.get(projectName);

        int companyId = projectDao.getCompanyIdByProjectId(projectId);
        if (companyId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid project selected!");
            return;
        }

        int positionId = getPositionIdByProjectId(projectId);
        if (positionId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "No valid position found for the selected project!");
            return;
        }

        int userId = SceneManager.getLoggedInUserId();
        if (userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to identify the logged-in user!");
            return;
        }

        Candidate candidate = new Candidate(0, name, surname, role, companyId, positionId, userId);
        if (candidateDao.addCandidate(candidate) > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Candidate added successfully!");
            if (dashboardController != null) {
                dashboardController.refreshCandidateTable();
                dashboardController.refreshProjectTable();
            }
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add candidate!");
        }
        loadProjectData();
    }


    private int getPositionIdByProjectId(int projectId) {
        String sql = "SELECT TOP 1 PositionID FROM ProjectPositions WHERE ProjectID = ?";
        try (var conn = DatabaseConnection.getConnection();
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (var rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("PositionID");
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching PositionID: " + e.getMessage());
        }
        return -1;
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        surnameField.clear();
        roleField.clear();
        projectComboBox.setValue(null);
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
