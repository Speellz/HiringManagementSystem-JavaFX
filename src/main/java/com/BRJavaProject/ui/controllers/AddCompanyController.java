package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CompanyDao;
import com.BRJavaProject.model.Company;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;
import java.util.List;

public class AddCompanyController {

    @FXML
    private TextField companyNameField;
    @FXML
    private DatePicker companyDatePicker;
    @FXML
    private TableView<Company> companyTable;

    private final CompanyDao companyDao = new CompanyDao();

    private DashboardController dashboardController;

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    private void handleAddCompany() {
        String companyName = companyNameField.getText();
        java.time.LocalDate localDate = companyDatePicker.getValue();

        if (companyName.isEmpty() || localDate == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        Company company = new Company();
        company.setCompanyName(companyName);
        company.setDate(Date.valueOf(localDate));
        company.setUserId(1);

        companyDao.addCompany(company);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Company added successfully!");

        if (dashboardController != null) {
            dashboardController.refreshCompanyTable(); // Tabloyu yenile
        }

        Stage stage = (Stage) companyNameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleClear() {
        companyNameField.clear();
        companyDatePicker.setValue(null);
    }

    private void refreshCompanyTable() {
        List<Company> companies = companyDao.getAllCompanies();
        ObservableList<Company> companyList = FXCollections.observableArrayList(companies);
        companyTable.setItems(companyList);
        companyTable.refresh();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
