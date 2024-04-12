package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.entities.User;
import hr.application.enums.AlertEnums;
import hr.application.enums.RegexEnums;
import hr.application.exceptions.UsernameExistsException;
import hr.application.scenemanagement.SceneManager;
import hr.application.scenemanagement.SceneType;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signUpButton;

    public void initialize() {
        tooltipInstallation();
        signUpButton.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty())
                .or(confirmPasswordField.textProperty().isEmpty()));
    }

    private void tooltipInstallation() {
        Tooltip usernameTooltip = new Tooltip("""
        Username must contain only letters and numbers.
        "e.g. leon123""");
        Tooltip.install(usernameField, usernameTooltip);

        Tooltip passwordTooltip = new Tooltip("""
                Password must contain at least one uppercase letter,
                one lowercase letter and one number
                e.g. Leon123""");
        Tooltip.install(passwordField, passwordTooltip);
    }

    public void signUp() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!inputsAreValid(username, password)) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Invalid input!",
                    "Please enter valid input!").createAlert().show();
            clearFields();
            return;
        }

        try {
            UserService.checkIfUsernameExists(username);
        } catch (UsernameExistsException e) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Username exists!",
                    "User with username " + username + " already exists!").createAlert().show();
            clearFields();
            return;
        }

        if (!confirmPasswordMatches(password, confirmPassword)) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Passwords don't match!",
                    "Please enter matching passwords!").createAlert().show();
            confirmPasswordField.clear();
            return;
        }

        User user = new User(username, password);
        startThreads(user);
        AlertFactory.getAlert(AlertEnums.INFO, "User created!",
                "User " + user.getUsername() + " created!").createAlert().show();
        SceneManager.getInstance().setScene(SceneType.LOGIN);
    }

    private void startThreads(User user) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture<Void> saveUser = CompletableFuture.runAsync(() -> DatabaseUtility.saveUser(user), executorService);
        CompletableFuture<Void> writeUserToFile = CompletableFuture.runAsync(() -> UserService.writeUserToFile(user), executorService);
        CompletableFuture.allOf(saveUser, writeUserToFile).thenRun(executorService::shutdown);
    }

    public void signInLink() {
        SceneManager.getInstance().setScene(SceneType.LOGIN);
    }

    private boolean inputsAreValid(String username, String password) {
        return RegexEnums.VALID_USERNAME.matches(username) && RegexEnums.VALID_PASSWORD.matches(password);
    }

    private boolean confirmPasswordMatches(String password, String confirmPassword) {
        return password.matches(confirmPassword);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

}
