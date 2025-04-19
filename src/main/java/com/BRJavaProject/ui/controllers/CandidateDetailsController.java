package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CandidateDetailsDao;
import com.BRJavaProject.database.ProjectDao;
import com.BRJavaProject.model.Candidate;
import com.BRJavaProject.model.CandidateDetails;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CandidateDetailsController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField roleField;

    @FXML
    private ComboBox<String> projectComboBox;

    @FXML
    private TextField statusField;

    @FXML
    private TextField statusExplanationField;

    @FXML
    private TextArea notesArea;

    @FXML
    private TextField currentCompanyField;

    private Candidate candidate;
    private CandidateDetails candidateDetails;
    private final CandidateDetailsDao candidateDetailsDao = new CandidateDetailsDao();
    private final ProjectDao projectDao = new ProjectDao();
    private Map<String, Integer> projectMap = new HashMap<>();

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
        loadProjectMap();
        loadCandidateDetails();
    }

    private void loadProjectMap() {
        List<String> projectList = projectDao.getAllProjectNames();
        for (String projectName : projectList) {
            int projectId = projectDao.getProjectIdByName(projectName);
            projectMap.put(projectName, projectId);
        }
    }

    private void loadCandidateDetails() {
        if (candidate != null) {
            nameField.setText(candidate.getName());
            surnameField.setText(candidate.getSurname());
            roleField.setText(candidate.getRole());

            List<String> projectList = projectDao.getAllProjectNames();
            projectComboBox.getItems().setAll(projectList);

            String currentProject = projectDao.getProjectNameById(candidate.getPositionId());
            if (currentProject != null) {
                projectComboBox.setValue(currentProject);
            }

            candidateDetails = candidateDetailsDao.getCandidateDetailsById(candidate.getId());
            if (candidateDetails != null) {
                statusField.setText(candidateDetails.getStatus());
                statusExplanationField.setText(candidateDetails.getStatusExplanation());
                notesArea.setText(candidateDetails.getNotes());
                currentCompanyField.setText(candidateDetails.getCurrentCompany()); // CurrentCompany set ediliyor
            }
        }
    }

    private void updateCompanyAndPosition(String projectName) {
        Map<String, Integer> companyMappings = projectDao.getCompanyMappings();
        Map<String, Integer> positionMappings = projectDao.getPositionMappings();

        int companyId = companyMappings.getOrDefault(projectName, 0);
        int positionId = positionMappings.getOrDefault(projectName, 0);

        candidate.setCompanyId(companyId);
        candidate.setPositionId(positionId);
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String role = roleField.getText();
        String projectName = projectComboBox.getValue();
        String currentCompany = currentCompanyField.getText();

        if (name.isEmpty() || surname.isEmpty() || role.isEmpty() || projectName == null || currentCompany.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields!");
            return;
        }

        Integer projectId = projectMap.get(projectName);
        if (projectId == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid project selected!");
            return;
        }

        int companyId = projectDao.getCompanyIdByProjectId(projectId);
        if (companyId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid company for the selected project!");
            return;
        }

        candidate.setName(name);
        candidate.setSurname(surname);
        candidate.setRole(role);
        candidate.setCompanyId(companyId);
        candidate.setPositionId(projectId);

        if (candidateDetails == null) {
            candidateDetails = new CandidateDetails(
                    0,
                    candidate.getId(),
                    statusField.getText(),
                    statusExplanationField.getText(),
                    notesArea.getText(),
                    currentCompany
            );
            candidateDetailsDao.addCandidateDetails(candidateDetails);
        } else {
            candidateDetails.setStatus(statusField.getText());
            candidateDetails.setStatusExplanation(statusExplanationField.getText());
            candidateDetails.setNotes(notesArea.getText());
            candidateDetails.setCurrentCompany(currentCompany);
            candidateDetailsDao.updateCandidateDetails(candidateDetails);
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "Candidate details updated successfully!");
        closeWindow();
    }

    @FXML
    private void handleDelete() {
        if (candidateDetails != null) {
            boolean success = candidateDetailsDao.deleteCandidateDetails(candidateDetails.getCandidateId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Candidate details deleted successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete candidate details!");
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
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
