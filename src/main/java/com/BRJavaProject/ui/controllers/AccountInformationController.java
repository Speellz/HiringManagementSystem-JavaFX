package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.UserDao;
import com.BRJavaProject.model.User;
import com.BRJavaProject.ui.SceneManager;
import com.BRJavaProject.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AccountInformationController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private int userId;

    private final UserDao userDao = new UserDao();
    private User loggedInUser;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void initialize() {
        loadUserData();
    }

    private void loadUserData() {
        userId = SceneManager.getLoggedInUserId();

        if (userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Error", "User ID is not set.");
            return;
        }

        loggedInUser = userDao.getLoggedInUser(userId);
        if (loggedInUser != null) {
            nameField.setText(loggedInUser.getName());
            surnameField.setText(loggedInUser.getSurname());
            emailField.setText(loggedInUser.getEmail());
            phoneField.setText(loggedInUser.getPhone());
            usernameField.setText(loggedInUser.getUsername());
            passwordField.setText(loggedInUser.getPassword());
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data.");
        }
    }

    @FXML
    private void handleSave() {
        if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() ||
                emailField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields must be filled.");
            return;
        }

        loggedInUser.setName(nameField.getText());
        loggedInUser.setSurname(surnameField.getText());
        loggedInUser.setEmail(emailField.getText());
        loggedInUser.setPhone(phoneField.getText());
        loggedInUser.setUsername(usernameField.getText());

        String hashedPassword = PasswordUtils.hashPassword(passwordField.getText());
        loggedInUser.setPassword(hashedPassword);

        boolean success = userDao.updateUser(loggedInUser);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User information updated successfully.");
            SceneManager.switchScene("profile.fxml", true, true);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user information.");
        }
    }

    @FXML
    private void handleDelete() {
        boolean success = userDao.deleteUser(loggedInUser.getUserId());
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
            SceneManager.logout();
            SceneManager.switchScene("login.fxml", false, false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete user.");
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("profile.fxml", false, false);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
