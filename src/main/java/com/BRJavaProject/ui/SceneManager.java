package com.BRJavaProject.ui;

import com.BRJavaProject.ui.controllers.AccountInformationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;
    private static int loggedInUserId;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFile, boolean maximized, boolean resizable) {
        if (!fxmlFile.startsWith("/")) {
            fxmlFile = "/com/BRJavaProject/ui/fxml/" + fxmlFile;
        }
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            scene.getStylesheets().clear();
            String css = SceneManager.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setScene(scene);

            if (fxmlFile.contains("dashboard.fxml") || fxmlFile.contains("add_candidate.fxml") || fxmlFile.contains("add_project.fxml")) {
                primaryStage.setMaximized(maximized);
                primaryStage.setResizable(resizable);
            } else {
                primaryStage.setMaximized(false);
                primaryStage.setResizable(resizable);
                primaryStage.setWidth(800);
                primaryStage.setHeight(600);
                primaryStage.centerOnScreen();
            }

            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }


    public static Stage createDialog(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
            Stage stage = new Stage();
            stage.setTitle(title);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);

            scene.getProperties().put("loader", loader);

            stage.initModality(Modality.APPLICATION_MODAL);
            return stage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void switchToAccountInformation(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/account_information.fxml"));
            Parent root = loader.load();
            AccountInformationController controller = loader.getController();
            controller.setUserId(userId);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setLoggedInUserId(int userId) {
        loggedInUserId = userId;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static void logout() {
        loggedInUserId = 0;
        switchScene("login.fxml", false, false);
    }
}
