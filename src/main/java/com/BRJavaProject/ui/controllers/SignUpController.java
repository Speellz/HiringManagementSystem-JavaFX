package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.UserDao;
import com.BRJavaProject.model.User;
import com.BRJavaProject.ui.SceneManager;
import com.BRJavaProject.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {

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

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private final UserDao userDao = new UserDao();

    @FXML
    private void handleLogin() {
        SceneManager.switchScene("login.fxml", false, false);
    }

    @FXML
    private void handleSignUp() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        try {

            User newUser = new User(0, name, surname, email, phone, username, password, "User");
            boolean isUserAdded = userDao.addUser(newUser);

            if (isUserAdded) {
                SceneManager.switchScene("login.fxml", false, true);
            } else {
                errorLabel.setText("User registration failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("An error occurred during registration.");
        }
    }
}

