package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.entities.User;
import hr.application.entities.UserSession;
import hr.application.enums.AlertEnums;
import hr.application.enums.UserRole;
import hr.application.scenemanagement.SceneManager;
import hr.application.scenemanagement.SceneType;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.PasswordHashing;
import hr.application.utilities.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class LogInController {
    private static final Logger logger = LoggerFactory.getLogger(LogInController.class);

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button logInButton;

    public void initialize() {
        logInButton.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty()));
    }

    public void logIn() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        Map<String, String> users = UserService.getUsersFromFile();
        if (users.isEmpty()) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Users not found!",
                    "Please contact the administrator!").createAlert().show();
            logger.error("Users not found!");
            return;
        }

        if (!users.containsKey(username)) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Invalid username!",
                    "Please enter valid username!").createAlert().show();
            clearFields();
            return;
        }

        if (!PasswordHashing.checkPassword(password, users.get(username))) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Invalid password!",
                    "Please enter valid password!").createAlert().show();
            passwordField.clear();
            return;
        }

        Optional<User> user = DatabaseUtility.getUser(username);
        user.ifPresentOrElse(value -> {
            UserSession.getInstance().setUser(value);
            logger.info(username + " logged in!");
            if (value.getRole().equals(UserRole.USER)) SceneManager.getInstance().setScene(SceneType.HOME);
            else SceneManager.getInstance().setScene(SceneType.ADMIN_ROUTES);
        }, () -> {
            AlertFactory.getAlert(AlertEnums.ERROR, "User not found!",
                    "User is not found in database!").createAlert().show();
            logger.error("User not found!");
        });
    }

    public void signUpLink() {
        SceneManager.getInstance().setScene(SceneType.SIGNUP);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
    }

}
