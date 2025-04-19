package com.BRJavaProject.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.BRJavaProject.database.CandidateDao;
import com.BRJavaProject.database.CompanyDao;
import com.BRJavaProject.database.UserDao;
import com.BRJavaProject.model.Candidate;
import com.BRJavaProject.model.Company;
import com.BRJavaProject.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    private final CandidateDao candidateDao = new CandidateDao();
    private final CompanyDao companyDao = new CompanyDao();
    private final UserDao userDao = new UserDao();
    private VBox mainLayout;
    private TableView<Candidate> candidatesTable;
    private TableView<Company> companiesTable;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");
        primaryStage.setWidth(400);  // Küçük pencere genişliği
        primaryStage.setHeight(300);  // Küçük pencere yüksekliği
        primaryStage.setResizable(false);  // Giriş ekranında yeniden boyutlandırma olmasın

        primaryStage.setOnCloseRequest(e -> {
            System.out.println("Application closed.");
            Platform.exit();  // Uygulama kapandığında çıkış yap
        });

        boolean loginSuccessful;
        do {
            loginSuccessful = showLoginDialog(primaryStage);  // Stage parametresi verildi
            if (!loginSuccessful) {
                System.out.println("Login cancelled. Exiting application.");
                Platform.exit();
                return;
            }
        } while (!loginSuccessful);

        // Giriş başarılıysa ana uygulamayı tam ekran modunda göster
        showMainLayout(primaryStage);  // Stage parametresi verildi
    }

    private void showLoginScene() {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        loginLayout.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> showSignUpScene());

        Button forgotPasswordButton = new Button("Forgot Password");
        forgotPasswordButton.setOnAction(e -> showForgotPasswordScene());

        loginLayout.getChildren().addAll(usernameField, passwordField, loginButton, signUpButton, forgotPasswordButton);
        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setScene(loginScene);
    }

    private void handleLogin(String username, String password) {
        User user = userDao.loginUser(username, password);
        if (user != null) {
            showMainLayout(primaryStage); // Başarılı girişte ana ekrana geç
        } else {
            showErrorDialog("Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private boolean showLoginDialog(Stage primaryStage) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Login");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        Button signUpButton = new Button("Sign Up");
        Button forgotPasswordButton = new Button("Forgot Password");

        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        VBox vbox = new VBox(10, usernameField, passwordField, forgotPasswordButton, signUpButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        signUpButton.setOnAction(e -> {
            dialog.close();
            showSignUpDialog(primaryStage);  // Stage parametresi verildi
        });

        forgotPasswordButton.setOnAction(e -> {
            dialog.close();
            showForgotPasswordDialog(primaryStage);  // Stage parametresi verildi
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                User user = userDao.loginUser(username, password);

                if (user == null) {
                    showErrorDialog("Login Failed", "Invalid username or password. Please try again.");
                    return null;
                } else {
                    return user;
                }
            }
            return null;
        });

        Optional<User> result = dialog.showAndWait();
        return result.isPresent();
    }

    private void showSignUpScene() {
        VBox signUpLayout = new VBox(10);
        signUpLayout.setPadding(new Insets(20));
        signUpLayout.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showLoginScene());

        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> handleSignUp(usernameField.getText(), passwordField.getText()));

        signUpLayout.getChildren().addAll(usernameField, passwordField, signUpButton, backButton);
        Scene signUpScene = new Scene(signUpLayout, 300, 200);
        primaryStage.setScene(signUpScene);
    }

    private void handleSignUp(String username, String password) {
        User newUser = new User(0, username, "", "", "", username, password, "User");
        userDao.addUser(newUser);
        showInfoDialog("Sign Up Successful", "You can now log in with your credentials.");
        showLoginScene();
    }

    private void showForgotPasswordScene() {
        VBox forgotPasswordLayout = new VBox(10);
        forgotPasswordLayout.setPadding(new Insets(20));
        forgotPasswordLayout.setAlignment(Pos.CENTER);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        Button resetButton = new Button("Reset Password");
        resetButton.setOnAction(e -> handlePasswordReset(usernameField.getText(), newPasswordField.getText()));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showLoginScene());

        forgotPasswordLayout.getChildren().addAll(usernameField, newPasswordField, resetButton, backButton);
        Scene forgotPasswordScene = new Scene(forgotPasswordLayout, 300, 200);
        primaryStage.setScene(forgotPasswordScene);
    }

    private void handlePasswordReset(String username, String newPassword) {
        if (userDao.resetPassword(username, newPassword)) {
            showInfoDialog("Password Reset", "Your password has been reset successfully.");
            showLoginScene();
        } else {
            showErrorDialog("Error", "Username not found.");
        }
    }

    private void showMainLayout(Stage primaryStage) {
        mainLayout = new VBox(10);
        showCandidatesList();  // Tabloları yükle

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);  // Tam ekran
        primaryStage.show();
    }

    private void showCandidatesList() {
        clearMainContent();

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setPadding(new Insets(10));

        Button addCandidateButton = new Button("Add Candidate");
        addCandidateButton.setOnAction(e -> showAddCandidateDialog());

        Button addCompanyButton = new Button("Add Company");
        addCompanyButton.setOnAction(e -> showAddCompanyDialog());

        searchBox.getChildren().addAll(addCandidateButton, addCompanyButton);

        candidatesTable = new TableView<>();
        setupCandidateTable();

        companiesTable = new TableView<>();
        setupCompanyTable();

        HBox tableLayout = new HBox(10, candidatesTable, companiesTable);
        tableLayout.setAlignment(Pos.CENTER);
        HBox.setHgrow(candidatesTable, Priority.ALWAYS);
        HBox.setHgrow(companiesTable, Priority.ALWAYS);

        mainLayout.getChildren().addAll(searchBox, tableLayout);
    }

    private void showAddCompanyDialog() {
        Dialog<Company> dialog = new Dialog<>();
        dialog.setTitle("Add Company");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        TextField companyNameField = new TextField();
        companyNameField.setPromptText("Company Name");

        TextField roleField = new TextField();
        roleField.setPromptText("Role");

        DatePicker dateField = new DatePicker();
        dateField.setPromptText("Establishment Date");
        dateField.setValue(LocalDate.now());

        VBox vbox = new VBox(10, companyNameField, roleField, dateField);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Company(
                        0,
                        companyNameField.getText(),
                        roleField.getText(),
                        java.sql.Date.valueOf(dateField.getValue())
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(company -> {
            companyDao.addCompany(company); // Veritabanına ekleme
            companiesTable.getItems().add(company); // Tabloyu güncelleme
        });
    }

    private void showSignUpDialog(Stage primaryStage) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Sign Up");

        ButtonType signUpButtonType = new ButtonType("Sign Up", ButtonBar.ButtonData.OK_DONE);
        Button backButton = new Button("Back");

        dialog.getDialogPane().getButtonTypes().addAll(signUpButtonType);

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        VBox vbox = new VBox(10, nameField, surnameField, emailField, phoneField, usernameField, passwordField, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        backButton.setOnAction(e -> {
            dialog.close();
            showLoginDialog(primaryStage);  // Giriş ekranına dön
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == signUpButtonType) {
                if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() ||
                        emailField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                        usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {

                    showErrorDialog("Invalid Input", "All fields are required!");
                    return null;
                }

                return new User(
                        0,
                        nameField.getText(),
                        surnameField.getText(),
                        emailField.getText(),
                        phoneField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        "User"
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            userDao.addUser(user);
            showInfoDialog("Sign Up Successful", "User registered successfully! You can now log in.");
        });
    }

    private void showForgotPasswordDialog(Stage primaryStage) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Forgot Password");

        ButtonType resetButtonType = new ButtonType("Reset Password", ButtonBar.ButtonData.OK_DONE);
        Button backButton = new Button("Back");

        dialog.getDialogPane().getButtonTypes().addAll(resetButtonType);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter your username");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");

        VBox vbox = new VBox(10, usernameField, newPasswordField, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(vbox);

        backButton.setOnAction(e -> {
            dialog.close();
            showLoginDialog(primaryStage);  // Giriş ekranına dön
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == resetButtonType) {
                String username = usernameField.getText();
                String newPassword = newPasswordField.getText();

                if (userDao.resetPassword(username, newPassword)) {
                    showInfoDialog("Password Reset Successful", "Your password has been reset successfully.");
                    return "Password reset successful";
                } else {
                    showErrorDialog("Password Reset Failed", "Username not found. Please try again.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showAddCandidateDialog() {
        Dialog<Candidate> dialog = new Dialog<>();
        dialog.setTitle("Add Candidate");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Aday eklemek için gerekli alanlar
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");

        TextField roleField = new TextField();
        roleField.setPromptText("Role");

        TextField statusField = new TextField();
        statusField.setPromptText("Status");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField notesField = new TextField();
        notesField.setPromptText("Notes");

        TextField experienceField = new TextField();
        experienceField.setPromptText("Experience (Years)");

        TextField ageField = new TextField();
        ageField.setPromptText("Age");

        TextField salaryField = new TextField();
        salaryField.setPromptText("Salary");

        VBox vbox = new VBox(10, nameField, surnameField, roleField, statusField, emailField,
                notesField, experienceField, ageField, salaryField);
        dialog.getDialogPane().setContent(vbox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    int experience = Integer.parseInt(experienceField.getText());
                    int age = Integer.parseInt(ageField.getText());
                    int salary = Integer.parseInt(salaryField.getText());

                    return new Candidate(
                            0,
                            nameField.getText(),
                            surnameField.getText(),
                            roleField.getText(),
                            statusField.getText(),
                            emailField.getText(),
                            notesField.getText(),
                            experience,
                            age,
                            salary
                    );
                } catch (NumberFormatException e) {
                    showErrorDialog("Invalid Input", "Experience, Age, and Salary must be numeric.");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(candidate -> {
            candidateDao.addCandidate(candidate); // Veritabanına ekleme
            candidatesTable.getItems().add(candidate); // Tabloyu güncelleme
        });
    }

    private void setupCandidateTable() {
        TableColumn<Candidate, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        TableColumn<Candidate, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        candidatesTable.getColumns().addAll(nameColumn, roleColumn);

        List<Candidate> candidates = candidateDao.getAllCandidates();
        candidatesTable.getItems().addAll(candidates);
    }

    private void setupCompanyTable() {
        TableColumn<Company, String> companyNameColumn = new TableColumn<>("Company Name");
        companyNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompanyName()));

        TableColumn<Company, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        companiesTable.getColumns().addAll(companyNameColumn, roleColumn);

        List<Company> companies = companyDao.getAllCompanies();
        companiesTable.getItems().addAll(companies);
    }

    private void clearMainContent() {
        mainLayout.getChildren().clear();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
