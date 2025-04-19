package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CompanyDao;
import com.BRJavaProject.model.Company;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;

public class EditCompanyController {
    @FXML
    private TextField companyNameField;
    @FXML
    private DatePicker companyDatePicker;

    private Company company;

    public void setCompany(Company company) {
        this.company = company;
        companyNameField.setText(company.getCompanyName());
        companyDatePicker.setValue(company.getDate().toLocalDate());
    }

    @FXML
    private void handleSave() {
        company.setCompanyName(companyNameField.getText());
        company.setDate(Date.valueOf(companyDatePicker.getValue()));

        CompanyDao companyDao = new CompanyDao();
        companyDao.updateCompany(company);

        ((Stage) companyNameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        ((Stage) companyNameField.getScene().getWindow()).close();
    }
}

