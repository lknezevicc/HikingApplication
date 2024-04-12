package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.alerts.BaseAlert;
import hr.application.entities.User;
import hr.application.entities.UserSession;
import hr.application.enums.AlertEnums;
import hr.application.enums.RegexEnums;
import hr.application.enums.UserRole;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.UserService;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersController {
    private final BaseAlert selectionAlert = AlertFactory.getAlert(AlertEnums.ERROR, "No user selected!",
            "Please select a user!");
    private List<User> users;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Long> userIDColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button createUserButton;
    @FXML
    private Button setRoleButton;
    @FXML
    private Button deleteUserButton;


    public void initialize() {
        users = DatabaseUtility.getUsers()
                .stream()
                .filter(user -> !user.getUsername().equals(UserSession.getInstance().getUser().getUsername()))
                .toList();
        usersTable.setItems(FXCollections.observableList(users));

        configureUI();
        addListeners();

        usersTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && userIsSelected()) {
                User selectedUser = usersTable.getSelectionModel().getSelectedItem();
                setRoleButton.setText(selectedUser.getRole().equals(UserRole.ADMIN) ? "SET AS USER" : "SET AS ADMIN");
            }
        });
    }

    public void setRole() {
        if (!userIsSelected()) {
            selectionAlert.createAlert().show();
            return;
        }

        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        switch (selectedUser.getRole()) {
            case ADMIN:
                selectedUser.setRole(UserRole.USER);
                setRoleButton.setText("SET AS ADMIN");
                break;
            case USER:
                selectedUser.setRole(UserRole.ADMIN);
                setRoleButton.setText("SET AS USER");
                break;
        }

        updateUserThreads(selectedUser);
        filterComboBox.getSelectionModel().clearSelection();
    }

    private void updateUserThreads(User selectedUser) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> DatabaseUtility.updateUserRole(selectedUser), executorService)
                .thenRun(() -> Platform.runLater(this::updateTable))
                .thenRun(executorService::shutdown);
    }

    public void createUser() {
        if (!inputsAreValid(usernameField.getText(), passwordField.getText())) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Invalid input!",
                    "Please enter valid input!").createAlert().show();
            clearFields();
            return;
        }

        if (usernameExists()) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Username already exists!",
                    "Please enter another username!").createAlert().show();
            usernameField.clear();
            return;
        }

        if (!passwordsMatch()) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Passwords don't match!",
                    "Please enter matching passwords!").createAlert().show();
            confirmPasswordField.clear();
            return;
        }

        User newUser = new User(usernameField.getText(), passwordField.getText());

        saveUserThreads(newUser);
        AlertFactory.getAlert(AlertEnums.INFO, "User created!",
                "User " + newUser.getUsername() + " created!").createAlert().show();
        clearFields();
    }

    private void saveUserThreads(User newUser) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture.runAsync(() -> DatabaseUtility.saveUser(newUser), executorService)
                .thenRunAsync(() -> UserService.writeUserToFile(newUser), executorService)
                .thenRun(() -> Platform.runLater(this::updateTable))
                .thenRun(executorService::shutdown);
    }

    public void deleteUser() {
        if (!userIsSelected()) {
            selectionAlert.createAlert().show();
            return;
        }

        Optional<ButtonType> alertResult =AlertFactory.getAlert(AlertEnums.CONFIRMATION, "Delete user",
                "Are you sure you want to delete this user?").createAlert().showAndWait();
        if (alertResult.isPresent() && alertResult.get().equals(ButtonType.OK)) deleteUserThreads();
    }

    private void deleteUserThreads() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture.runAsync(() -> DatabaseUtility.deleteUser(selectedUser), executorService)
                .thenRunAsync(() -> UserService.deleteUserFromFile(selectedUser.getUsername()), executorService)
                .thenRun(() -> Platform.runLater(this::updateTable))
                .thenRun(executorService::shutdown);
    }

    private void updateTable() {
        users = DatabaseUtility.getUsers()
                .stream()
                .filter(user -> !user.getUsername().equals(UserSession.getInstance().getUser().getUsername()))
                .toList();
        usersTable.setItems(FXCollections.observableList(users));
    }

    private void filter(String searchValue) {
        if (!filterComboBox.getSelectionModel().isEmpty()) {
            usersTable.setItems(FXCollections.observableList(
                    users.stream()
                            .filter(user -> user.getUsername().toLowerCase().contains(searchValue.toLowerCase()))
                            .filter(user -> user.getRole().name().equals(filterComboBox.getSelectionModel().getSelectedItem()))
                            .toList()));
        } else {
            usersTable.setItems(FXCollections.observableList(
                    users.stream()
                            .filter(user -> user.getUsername().toLowerCase().contains(searchValue.toLowerCase()))
                            .toList()));
        }
    }

    private boolean passwordsMatch() {
        return passwordField.getText().equals(confirmPasswordField.getText());
    }

    private boolean usernameExists() {
        return users.stream().anyMatch(user -> user.getUsername().equals(usernameField.getText()));
    }

    private boolean inputsAreValid(String username, String password) {
        return RegexEnums.VALID_USERNAME.matches(username) && RegexEnums.VALID_PASSWORD.matches(password);
    }

    private boolean userIsSelected() {
        return !usersTable.getSelectionModel().isEmpty();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void configureUI() {
        filterComboBox.setItems(FXCollections.observableList(Arrays.stream(UserRole.values()).map(UserRole::name).toList()));
        userIDColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getID()).asObject());
        usernameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        userRoleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole().name()));
    }

    private void addListeners() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filter(newValue));
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(searchField.getText()));

        createUserButton.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty())
                .or(confirmPasswordField.textProperty().isEmpty()));

        setRoleButton.disableProperty().bind(usersTable.getSelectionModel().selectedItemProperty().isNull());
        deleteUserButton.disableProperty().bind(usersTable.getSelectionModel().selectedItemProperty().isNull());
    }

}
