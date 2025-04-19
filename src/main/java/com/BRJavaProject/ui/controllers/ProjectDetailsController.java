package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.ProjectDao;
import com.BRJavaProject.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class ProjectDetailsController {

    @FXML
    private TextField projectNameField;

    @FXML
    private TextField projectIdField;

    @FXML
    private TextField companyField;

    @FXML
    private TextField userField;

    @FXML
    private TextArea projectDescriptionArea;

    private Project project;
    private final ProjectDao projectDao = new ProjectDao();

    public void setProject(Project project) {
        this.project = project;
        loadProjectDetails();
    }

    private void loadProjectDetails() {
        if (project != null) {
            projectNameField.setText(project.getProjectName());
            projectIdField.setText(String.valueOf(project.getId()));
            companyField.setText(projectDao.getCompanyNameById(project.getCompanyId()));
            userField.setText(projectDao.getUserNameById(project.getUserId()));
            projectDescriptionArea.setText(project.getDescription());
        }
    }

    @FXML
    private void handleSave() {
        String projectName = projectNameField.getText();
        String companyName = companyField.getText();
        String userName = userField.getText();
        String description = projectDescriptionArea.getText();

        if (projectName.isEmpty() || companyName.isEmpty() || userName.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields!");
            return;
        }

        project.setProjectName(projectName);
        project.setCompanyId(projectDao.getCompanyIdByName(companyName));
        project.setUserId(projectDao.getUserIdByName(userName));
        project.setDescription(description);

        boolean success = projectDao.updateProject(project);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Project details updated successfully!");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update project details!");
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Project");
        confirmationAlert.setHeaderText("Are you sure?");
        confirmationAlert.setContentText("This action cannot be undone.");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = projectDao.deleteProject(project.getId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Project deleted successfully!");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete project!");
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) projectNameField.getScene().getWindow();
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
