package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.UserDao;
import com.BRJavaProject.model.User;
import com.BRJavaProject.ui.SceneManager;
import com.BRJavaProject.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;

public class LoginController {
    private final UserDao userDao = new UserDao();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in both fields.");
            return;
        }

        User user = userDao.loginUser(username, password);

        if (user != null) {
            SceneManager.setLoggedInUserId(user.getUserId());
            SceneManager.switchScene("dashboard.fxml", true, true);
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }

    @FXML
    private void handleSignUp() {
        SceneManager.switchScene("sign_up.fxml",false,false);
    }

    @FXML
    private void handleForgotPassword() {
        SceneManager.switchScene("forgot_password.fxml",false,false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
