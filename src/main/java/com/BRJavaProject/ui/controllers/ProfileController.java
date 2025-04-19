package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ProfileController {
    @FXML
    private void handleAccountInformation() {
        SceneManager.switchScene("account_information.fxml", true, true);
    }

    @FXML
    private void handleSkills() {
        SceneManager.switchScene("skills.fxml", true, true);
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("dashboard.fxml", true, true);
    }
}
