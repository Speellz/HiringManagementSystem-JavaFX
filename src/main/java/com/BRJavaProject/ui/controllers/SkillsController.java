package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.SkillDao;
import com.BRJavaProject.model.Skill;
import com.BRJavaProject.ui.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class SkillsController {

    @FXML
    private TableView<Skill> skillsTable;

    @FXML
    private TableColumn<Skill, String> skillNameColumn;

    @FXML
    private TableColumn<Skill, String> proficiencyLevelColumn;

    @FXML
    private TableColumn<Skill, String> certificationColumn;

    @FXML
    private TableColumn<Skill, Void> actionsColumn;

    private final SkillDao skillDao = new SkillDao();
    private ObservableList<Skill> skillList;

    public void initialize() {
        setupTable();
        loadSkills();
    }

    private void setupTable() {
        skillNameColumn.setCellValueFactory(new PropertyValueFactory<>("skillName"));
        proficiencyLevelColumn.setCellValueFactory(new PropertyValueFactory<>("proficiencyLevel"));
        certificationColumn.setCellValueFactory(new PropertyValueFactory<>("certification"));
        actionsColumn.setCellFactory(column -> new TableCellWithButtons());

        skillsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadSkills() {
        int loggedInUserId = SceneManager.getLoggedInUserId();
        List<Skill> skills = skillDao.getSkillsByUserId(loggedInUserId);
        skillList = FXCollections.observableArrayList(skills);
        skillsTable.setItems(skillList);
    }

    @FXML
    private void handleAddSkill() {
        Dialog<Skill> dialog = new Dialog<>();
        dialog.setTitle("Add Skill");
        dialog.setHeaderText("Enter skill details:");

        TextField skillNameField = new TextField();
        skillNameField.setPromptText("Skill Name");

        TextField proficiencyLevelField = new TextField();
        proficiencyLevelField.setPromptText("Proficiency Level");

        TextField certificationField = new TextField();
        certificationField.setPromptText("Certification");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Skill Name:"), 0, 0);
        grid.add(skillNameField, 1, 0);
        grid.add(new Label("Proficiency Level:"), 0, 1);
        grid.add(proficiencyLevelField, 1, 1);
        grid.add(new Label("Certification:"), 0, 2);
        grid.add(certificationField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Skill(skillNameField.getText(), proficiencyLevelField.getText(), certificationField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(skill -> {
            int loggedInUserId = SceneManager.getLoggedInUserId();
            skill.setUserId(loggedInUserId);

            boolean success = skillDao.addSkill(skill);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Skill added successfully.");
                loadSkills();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add skill.");
            }
        });
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

    private class TableCellWithButtons extends TableCell<Skill, Void> {
        private final Button editButton = new Button("Edit");
        private final Button deleteButton = new Button("Delete");
        private final HBox buttonBox = new HBox(10, editButton, deleteButton);

        public TableCellWithButtons() {
            editButton.getStyleClass().add("table-button");
            deleteButton.getStyleClass().add("table-button");

            editButton.setOnAction(event -> {
                Skill skill = getTableView().getItems().get(getIndex());
                handleEditSkill(skill);
            });

            deleteButton.setOnAction(event -> {
                Skill skill = getTableView().getItems().get(getIndex());
                handleDeleteSkill(skill);
            });

            buttonBox.setStyle("-fx-alignment: center;");
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                setGraphic(buttonBox);
            }
        }
    }

    private void handleEditSkill(Skill skill) {
        Dialog<Skill> dialog = new Dialog<>();
        dialog.setTitle("Edit Skill");
        dialog.setHeaderText("Update skill details:");

        TextField skillNameField = new TextField(skill.getSkillName());
        TextField proficiencyLevelField = new TextField(skill.getProficiencyLevel());
        TextField certificationField = new TextField(skill.getCertification());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Skill Name:"), 0, 0);
        grid.add(skillNameField, 1, 0);
        grid.add(new Label("Proficiency Level:"), 0, 1);
        grid.add(proficiencyLevelField, 1, 1);
        grid.add(new Label("Certification:"), 0, 2);
        grid.add(certificationField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                skill.setSkillName(skillNameField.getText());
                skill.setProficiencyLevel(proficiencyLevelField.getText());
                skill.setCertification(certificationField.getText());
                return skill;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedSkill -> {
            boolean success = skillDao.updateSkill(updatedSkill);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Skill updated successfully.");
                loadSkills();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update skill.");
            }
        });
    }

    private void handleDeleteSkill(Skill skill) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Skill");
        alert.setHeaderText("Are you sure you want to delete this skill?");
        alert.setContentText(skill.getSkillName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = skillDao.deleteSkill(skill.getSkillId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Skill deleted successfully.");
                    skillList.remove(skill);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete skill.");
                }
            }
        });
    }
}
