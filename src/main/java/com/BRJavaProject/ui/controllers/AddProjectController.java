package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CompanyDao;
import com.BRJavaProject.database.ProjectDao;
import com.BRJavaProject.model.Project;
import com.BRJavaProject.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AddProjectController {

    @FXML
    private TextField projectNameField;

    @FXML
    private ComboBox<String> companyComboBox;

    @FXML
    private DatePicker projectDatePicker;

    @FXML
    private TextField positionNameField;

    @FXML
    private TextField candidateCountField;

    private final CompanyDao companyDao = new CompanyDao();
    private final ProjectDao projectDao = new ProjectDao();

    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        loadCompanies();
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    private void loadCompanies() {
        List<String> companyNames = companyDao.getAllCompanies()
                .stream()
                .map(company -> company.getCompanyName())
                .collect(Collectors.toList());
        companyComboBox.getItems().addAll(companyNames);
    }

    @FXML
    private void handleAddProject() {
        String projectName = projectNameField.getText();
        String selectedCompany = companyComboBox.getValue();
        java.time.LocalDate localDate = projectDatePicker.getValue();
        String positionName = positionNameField.getText();
        String candidateCountStr = candidateCountField.getText();

        if (projectName.isEmpty() || selectedCompany == null || localDate == null || positionName.isEmpty() || candidateCountStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        int candidateCount;
        try {
            candidateCount = Integer.parseInt(candidateCountStr);
            if (candidateCount < 1) {
                throw new NumberFormatException("Candidate count must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Candidate count must be a positive number.");
            return;
        }

        int companyId = companyDao.getAllCompanies()
                .stream()
                .filter(company -> company.getCompanyName().equals(selectedCompany))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Company not found"))
                .getCompanyId();

        Project project = new Project();
        project.setProjectName(projectName);
        project.setCompanyId(companyId);
        project.setProjectDate(Date.valueOf(localDate));
        int projectId = projectDao.addProjectAndGetId(project);

        if (projectId > 0) {
            projectDao.addPositionToProject(projectId, positionName, candidateCount);

            if (dashboardController != null) {
                dashboardController.refreshProjectTable();
                dashboardController.refreshCandidateTable();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "Project and position added successfully!");

            ((Stage) projectNameField.getScene().getWindow()).close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add project.");
        }
    }

    @FXML
    private void handleClear() {
        projectNameField.clear();
        companyComboBox.getSelectionModel().clearSelection();
        projectDatePicker.setValue(null);
        positionNameField.clear();
        candidateCountField.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}