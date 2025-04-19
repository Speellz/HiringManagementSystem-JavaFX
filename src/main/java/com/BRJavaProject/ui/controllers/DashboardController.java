package com.BRJavaProject.ui.controllers;

import com.BRJavaProject.database.CandidateDao;
import com.BRJavaProject.database.CompanyDao;
import com.BRJavaProject.database.ProjectDao;
import com.BRJavaProject.model.Candidate;
import com.BRJavaProject.model.Company;
import com.BRJavaProject.model.Project;
import com.BRJavaProject.ui.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    // Candidate Table
    @FXML
    private TableView<Candidate> candidateTable;

    @FXML
    private TableColumn<Candidate, String> candidateNameColumn;

    @FXML
    private TableColumn<Candidate, String> candidateSurnameColumn;

    @FXML
    private TableColumn<Candidate, String> candidateRoleColumn;

    @FXML
    private TableColumn<Candidate, String> candidateCompanyColumn;

    @FXML
    private TableColumn<Candidate, String> candidateAssignedProjectColumn;


    @FXML
    private TableColumn<Candidate, String> candidateAddedByColumn;

    @FXML
    private TextField candidateSearchField;


    // Project Table
    @FXML
    private TableView<Project> projectTable;

    @FXML
    private TableColumn<Project, String> projectNameColumn;

    @FXML
    private TableColumn<Project, String> projectCompanyColumn;

    @FXML
    private TableColumn<Project, String> projectPositionColumn;


    @FXML
    private TableColumn<Project, String> projectDateColumn;

    @FXML
    private TextField projectSearchField;

    @FXML
    private TableColumn<Project, Integer> assignedCandidatesColumn;

    @FXML
    private TableColumn<Project, Integer> remainingCandidatesColumn;

    @FXML
    private VBox addProjectDialog;


    // Company Table
    @FXML
    private TableView<Company> companyTable;

    @FXML
    private TableColumn<Company, String> companyNameColumn;

    @FXML
    private TableColumn<Company, String> dateColumn;

    @FXML
    private TextField companySearchField;

    @FXML
    private TextField searchAllField;

    private final CandidateDao candidateDao = new CandidateDao();
    private final ProjectDao projectDao = new ProjectDao();
    private final CompanyDao companyDao = new CompanyDao();

    public void initialize() {
        // Candidate Table
        candidateNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        candidateSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        candidateRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        candidateAssignedProjectColumn.setCellValueFactory(new PropertyValueFactory<>("assignedProject"));
        candidateAddedByColumn.setCellValueFactory(new PropertyValueFactory<>("addedByName"));

        candidateTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Project Table
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        projectCompanyColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        projectPositionColumn.setCellValueFactory(new PropertyValueFactory<>("positionName"));
        projectDateColumn.setCellValueFactory(new PropertyValueFactory<>("projectDate"));
        assignedCandidatesColumn.setCellValueFactory(new PropertyValueFactory<>("assignedCandidates"));
        remainingCandidatesColumn.setCellValueFactory(new PropertyValueFactory<>("remainingCandidates"));

        projectTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // Company Table
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("companyName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        companyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setupCandidateSearch();
        setupCompanySearch();
        setupProjectSearch();
        setupSearchAll();

        setupCandidateActions();
        setupProjectActions();
        setupCompanyActions();

        loadProjects();
    }

    //Candidate
    private void setupCandidateSearch() {
        List<Candidate> candidates = candidateDao.getAllCandidates();
        ObservableList<Candidate> candidateList = FXCollections.observableArrayList(candidates);
        FilteredList<Candidate> filteredCandidates = new FilteredList<>(candidateList, b -> true);

        candidateSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCandidates.setPredicate(candidate -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (candidate.getName() != null && candidate.getName().toLowerCase().contains(lowerCaseFilter)) ||
                        (candidate.getSurname() != null && candidate.getSurname().toLowerCase().contains(lowerCaseFilter)) ||
                        (candidate.getRole() != null && candidate.getRole().toLowerCase().contains(lowerCaseFilter)) ||
                        (candidate.getAssignedProject() != null && candidate.getAssignedProject().toLowerCase().contains(lowerCaseFilter));
            });

            candidateTable.refresh();
        });

        candidateTable.setItems(filteredCandidates);
    }

    @FXML
    private void handleAddCandidate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/add_candidate.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Add Candidate");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            refreshCandidateTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshProjectTable();
    }

    @FXML
    private TableColumn<Candidate, Void> candidateActionColumn;

    private void setupCandidateActions() {
        candidateActionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button detailsButton = new Button("Details");

            {
                editButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");
                deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");
                detailsButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");

                editButton.setOnAction(event -> {
                    Candidate candidate = getTableView().getItems().get(getIndex());
                    handleEditCandidate(candidate);
                    candidateTable.refresh();
                });

                deleteButton.setOnAction(event -> {
                    Candidate candidate = getTableView().getItems().get(getIndex());
                    handleDeleteCandidate(candidate);
                    candidateTable.refresh();
                });

                detailsButton.setOnAction(event -> {
                    Candidate candidate = getTableView().getItems().get(getIndex());
                    handleCandidateDetails(candidate);
                });

                HBox buttons = new HBox(5, editButton, deleteButton, detailsButton);
                setGraphic(buttons);
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton, deleteButton, detailsButton));
                }
            }
        });
    }

    private void handleCandidateDetails(Candidate candidate) {
        Stage dialog = SceneManager.createDialog("/com/BRJavaProject/ui/fxml/candidate_details.fxml", "Candidate Details");
        if (dialog != null) {
            Scene scene = dialog.getScene();
            FXMLLoader loader = (FXMLLoader) scene.getProperties().get("loader");
            if (loader != null) {
                CandidateDetailsController controller = loader.getController();
                controller.setCandidate(candidate);
                dialog.showAndWait();
                setupCandidateSearch();
                setupProjectSearch();
            } else {
                System.err.println("FXMLLoader not found in scene properties!");
            }
        }
    }

    private void handleEditCandidate(Candidate candidate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/edit_candidate.fxml"));
            Parent root = loader.load();

            EditCandidateController controller = loader.getController();
            controller.loadCandidate(candidate);

            Stage stage = new Stage();
            stage.setTitle("Edit Candidate");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            setupCandidateSearch();
            setupProjectSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteCandidate(Candidate candidate) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this candidate?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                candidateDao.deleteCandidate(candidate.getId());

                setupCandidateSearch();

                refreshProjectTable();
            }
        });
    }

    public void refreshCandidateTable() {
        if (candidateTable != null) {
            List<Candidate> candidates = candidateDao.getAllCandidates();
            ObservableList<Candidate> candidateList = FXCollections.observableArrayList(candidates);
            candidateTable.setItems(candidateList);
        }
    }

    //

    //Project
    private void loadProjects() {
        List<Project> projects = projectDao.getAllProjects();
        ObservableList<Project> projectObservableList = FXCollections.observableArrayList(projects);

        projectObservableList.forEach(project -> {
            int assignedCandidates = candidateDao.getAssignedCandidatesCount(project.getId());
            int totalCandidates = project.getCandidateCount();
            project.setAssignedCandidates(assignedCandidates);
            project.setRemainingCandidates(totalCandidates - assignedCandidates);

            String positionNames = projectDao.getPositionNamesByProjectId(project.getId());
            project.setPositionName(positionNames != null ? positionNames : "No Position Available");
        });

        projectTable.setItems(projectObservableList);
        projectTable.refresh();
    }

    private void setupProjectSearch() {
        List<Project> projects = projectDao.getAllProjects();
        ObservableList<Project> projectList = FXCollections.observableArrayList(projects);
        FilteredList<Project> filteredProjects = new FilteredList<>(projectList, b -> true);

        projectSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredProjects.setPredicate(project -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (project.getProjectName() != null && project.getProjectName().toLowerCase().contains(lowerCaseFilter)) ||
                        (project.getCompanyName() != null && project.getCompanyName().toLowerCase().contains(lowerCaseFilter)) ||
                        (project.getProjectDate() != null && project.getProjectDate().toString().toLowerCase().contains(lowerCaseFilter)) ||
                        (project.getPositionName() != null && project.getPositionName().toLowerCase().contains(lowerCaseFilter)); // PositionName kontrolü
            });

            projectTable.refresh();
        });

        projectTable.setItems(filteredProjects);
    }

    private void setupProjectActions() {
        projectActionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            //private final Button detailsButton = new Button("Details");
            //

            {
                editButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");
                deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");
                //detailsButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");

                editButton.setOnAction(event -> {
                    Project project = getTableView().getItems().get(getIndex());
                    handleEditProject(project);
                });

                deleteButton.setOnAction(event -> {
                    Project project = getTableView().getItems().get(getIndex());
                    handleDeleteProject(project);
                });

                //detailsButton.setOnAction(event -> {Project project = getTableView().getItems().get(getIndex())handleProjectDetail(project);});

                HBox buttons = new HBox(5, editButton, deleteButton); //detailsButton
                setGraphic(buttons);
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton, deleteButton));
                }
            }
        });
    }


    @FXML
    private void handleAddProject() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/add_project.fxml"));
            Parent root = loader.load();

            AddProjectController controller = loader.getController();
            controller.setDashboardController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Add Project");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();

            refreshProjectTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSave() {
        System.out.println("Save button clicked.");
        addProjectDialog.setVisible(false);
    }

    @FXML
    private TableColumn<Project, Void> projectActionColumn;

    @FXML
    private void handleEditProject(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/edit_project.fxml"));
            Parent root = loader.load();

            EditProjectController controller = loader.getController();
            controller.loadProject(project);

            Stage stage = new Stage();
            stage.setTitle("Edit Project");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteProject(Project project) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this project?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                projectDao.deleteProject(project.getId());
                loadProjects();
            }
        });
    }

    @FXML
    private void handleProjectDetail(Project project) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/project_details.fxml"));
            Parent root = loader.load();

            ProjectDetailsController controller = loader.getController();
            controller.setProject(project);

            Stage stage = new Stage();
            stage.setTitle("Project Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadProjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void refreshProjectTable() {
        List<Project> projects = projectDao.getAllProjects();
        ObservableList<Project> projectList = FXCollections.observableArrayList(projects);
        projectTable.setItems(projectList);
    }


    //Company
    private void setupCompanySearch() {
        List<Company> companies = companyDao.getAllCompanies();
        ObservableList<Company> companyList = FXCollections.observableArrayList(companies);
        FilteredList<Company> filteredCompanies = new FilteredList<>(companyList, b -> true);

        companySearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredCompanies.setPredicate(company -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (company.getCompanyName() != null && company.getCompanyName().toLowerCase().contains(lowerCaseFilter)) ||
                        (company.getDate() != null && company.getDate().toString().toLowerCase().contains(lowerCaseFilter));
            });

            companyTable.refresh();
        });

        companyTable.setItems(filteredCompanies);
    }

    @FXML
    private void handleAddCompany() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/add_company.fxml"));
            Parent root = loader.load();

            AddCompanyController controller = loader.getController();
            controller.setDashboardController(this);

            Stage dialog = new Stage();
            dialog.setTitle("Add Company");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditCompany(Company company) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BRJavaProject/ui/fxml/edit_company.fxml"));
            Parent root = loader.load();

            EditCompanyController controller = loader.getController();
            controller.setCompany(company);

            Stage stage = new Stage();
            stage.setTitle("Edit Company");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshCompanyTable();
            refreshCompanyTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupCompanyActions() {
        companyActionColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");
                deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 5; -fx-min-width: 60px;");

                editButton.setOnAction(event -> {
                    Company company = getTableView().getItems().get(getIndex());
                    handleEditCompany(company);
                });

                deleteButton.setOnAction(event -> {
                    Company company = getTableView().getItems().get(getIndex());
                    handleDeleteCompany(company);
                });

                HBox buttons = new HBox(5, editButton, deleteButton);
                setGraphic(buttons);
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editButton, deleteButton));
                }
            }
        });
    }

    private void handleDeleteCompany(Company company) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this company?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                companyDao.deleteCompany(company.getCompanyId());
                refreshCompanyTable();
            }
        });
    }

    public void refreshCompanyTable() {
        List<Company> companies = companyDao.getAllCompanies();
        ObservableList<Company> companyList = FXCollections.observableArrayList(companies);
        companyTable.setItems(companyList);
    }
    //

    private void setupSearchAll() {
        List<Candidate> candidates = candidateDao.getAllCandidates();
        List<Project> projects = projectDao.getAllProjects();
        List<Company> companies = companyDao.getAllCompanies();

        ObservableList<Candidate> candidateList = FXCollections.observableArrayList(candidates);
        ObservableList<Project> projectList = FXCollections.observableArrayList(projects);
        ObservableList<Company> companyList = FXCollections.observableArrayList(companies);

        FilteredList<Candidate> filteredCandidates = new FilteredList<>(candidateList, b -> true);
        FilteredList<Project> filteredProjects = new FilteredList<>(projectList, b -> true);
        FilteredList<Company> filteredCompanies = new FilteredList<>(companyList, b -> true);

        searchAllField.textProperty().addListener((observable, oldValue, newValue) -> {
            String lowerCaseFilter = newValue.toLowerCase();

            filteredCandidates.setPredicate(candidate -> candidate.getName().toLowerCase().contains(lowerCaseFilter) ||
                    candidate.getSurname().toLowerCase().contains(lowerCaseFilter) ||
                    candidate.getRole().toLowerCase().contains(lowerCaseFilter));

            filteredProjects.setPredicate(project -> project.getProjectName().toLowerCase().contains(lowerCaseFilter) ||
                    project.getCompanyName().toLowerCase().contains(lowerCaseFilter) ||
                    project.getProjectDate().toString().toLowerCase().contains(lowerCaseFilter));

            filteredCompanies.setPredicate(company -> company.getCompanyName().toLowerCase().contains(lowerCaseFilter) ||
                    company.getDate().toString().toLowerCase().contains(lowerCaseFilter));

            candidateTable.setItems(filteredCandidates);
            projectTable.setItems(filteredProjects);
            companyTable.setItems(filteredCompanies);
        });
    }

    @FXML
    private void handleCancelDialog() {
        System.out.println("Cancel button clicked.");
        addProjectDialog.setVisible(false);

    }

    @FXML
    private TableColumn<Company, Void> companyActionColumn;


    //

    @FXML
    private void handleLogout() {
        SceneManager.logout();
    }

    @FXML
    private void handleProfile() {
        SceneManager.switchScene("profile.fxml", true, true);
    }

}
