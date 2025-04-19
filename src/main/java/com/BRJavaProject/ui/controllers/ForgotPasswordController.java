package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.UserDao;
import com.BRJavaProject.ui.SceneManager;
import com.BRJavaProject.utils.EmailSender;
import com.BRJavaProject.utils.OTPGenerator;
import com.BRJavaProject.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ForgotPasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField otpField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private final UserDao userDao = new UserDao();

    @FXML
    private void handleForgotPassword() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            System.out.println("Please enter your email address.");
            return;
        }

        if (userDao.emailExists(email)) {
            String otp = OTPGenerator.generateOTP(6); // 6 haneli OTP oluştur
            userDao.saveOTP(email, otp);
            EmailSender.sendEmail(email, "Password Reset OTP", "Your OTP: " + otp);
            System.out.println("OTP sent to " + email);
        } else {
            System.out.println("Email does not exist in the system.");
            return;
        }
        SceneManager.switchScene("login.fxml", false, true);
    }

    @FXML
    private void handleSendOTP() {
        String email = emailField.getText();

        if (email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter your email!");
            return;
        }

        if (!userDao.emailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Email does not exist in our records!");
            return;
        }

        String otp = OTPGenerator.generateOTP(6);
        userDao.saveOTP(email, otp);

        String subject = "Your OTP Code";
        String messageText = "Your OTP code is: " + otp;

        String result = EmailSender.sendEmail(email, subject, messageText);
        if (result.contains("başarıyla")) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "OTP has been sent to your email!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to send OTP. Please try again later.");
        }
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String otp = otpField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required!");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match!");
            return;
        }

        if (!userDao.verifyOTP(email, otp)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid OTP!");
            return;
        }

        try {
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            userDao.updatePassword(email, hashedPassword);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password reset successfully!");
            SceneManager.switchScene("login.fxml", false, true);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during password reset.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
