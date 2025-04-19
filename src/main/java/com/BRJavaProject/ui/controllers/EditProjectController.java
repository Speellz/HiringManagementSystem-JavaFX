package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CompanyDao;
import com.BRJavaProject.database.ProjectDao;
import com.BRJavaProject.model.Project;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.List;

public class EditProjectController {
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
    private Project project;

    public void loadProject(Project project) {
        this.project = project;
        projectNameField.setText(project.getProjectName());
        companyComboBox.setValue(project.getCompanyName());
        projectDatePicker.setValue(project.getProjectDate().toLocalDate());

        String positionName = projectDao.getPositionNamesByProjectId(project.getId());
        positionNameField.setText(positionName != null ? positionName : "");

        candidateCountField.setText(String.valueOf(project.getCandidateCount()));
        loadCompanies();
    }


    private void loadCompanies() {
        List<String> companyNames = companyDao.getAllCompanies()
                .stream()
                .map(company -> company.getCompanyName())
                .toList();
        companyComboBox.setItems(FXCollections.observableArrayList(companyNames));
    }

    @FXML
    private void handleSave() {
        String projectName = projectNameField.getText();
        String selectedCompany = companyComboBox.getValue();
        java.time.LocalDate projectDate = projectDatePicker.getValue();
        String positionName = positionNameField.getText();
        String candidateCountText = candidateCountField.getText();

        if (projectName.isEmpty() || selectedCompany == null || projectDate == null || positionName.isEmpty() || candidateCountText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        int candidateCount;
        try {
            candidateCount = Integer.parseInt(candidateCountText);
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

        project.setProjectName(projectName);
        project.setCompanyId(companyId);
        project.setProjectDate(Date.valueOf(projectDate));
        project.setPositionName(positionName);
        project.setCandidateCount(candidateCount);

        boolean success = projectDao.updateProject(project);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project updated successfully.");

        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update project.");
        }

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleCancel() {
        Stage stage = (Stage) projectNameField.getScene().getWindow();
        stage.close();
    }
}
