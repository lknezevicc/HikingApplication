package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.entities.UserSession;
import hr.application.enums.AlertEnums;
import hr.application.scenemanagement.SceneManager;
import hr.application.scenemanagement.SceneType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class SidebarController {
    private static final Logger logger = LoggerFactory.getLogger(SidebarController.class);

    @FXML
    private Label usernameLabel;
    @FXML
    private Button homeButton;
    @FXML
    private Button addRouteButton;
    @FXML
    private Button myRoutesButton;
    @FXML
    private Button routesButton;
    @FXML
    private Button usersButton;
    @FXML
    private Button changeLogButton;

    public void initialize() {
        usernameLabel.setText(UserSession.getInstance().getUser().getUsername());

        if (UserSession.getInstance().isAdmin()) {
            adminSidebarConfig();
        } else {
            userSidebarConfig();
        }
    }

    public void homeButton() {
        SceneManager.getInstance().setScene(SceneType.HOME);
    }
    public void addRouteButton() {
        SceneManager.getInstance().setScene(SceneType.ADD_ROUTE);
    }
    public void myRoutesButton() {
        SceneManager.getInstance().setScene(SceneType.MY_ROUTES);
    }

    public void routesButton() {
        SceneManager.getInstance().setScene(SceneType.ADMIN_ROUTES);
    }
    public void usersButton() {
        SceneManager.getInstance().setScene(SceneType.USERS);
    }
    public void changeLogButton() {
        SceneManager.getInstance().setScene(SceneType.CHANGELOG);
    }

    public void logoutButton() {
        Optional<ButtonType> alertResult = AlertFactory.getAlert(AlertEnums.CONFIRMATION, "Logout",
                "Are you sure you want to logout?").createAlert().showAndWait();

        if (alertResult.isPresent() && alertResult.get() == ButtonType.OK) {
            logger.info("User {} logged out", UserSession.getInstance().getUser().getUsername());
            UserSession.getInstance().cleanUserSession();
            SceneManager.getInstance().setScene(SceneType.LOGIN);
        }
    }

    private void adminSidebarConfig() {
        homeButton.setVisible(false);
        addRouteButton.setVisible(false);
        myRoutesButton.setVisible(false);
    }

    private void userSidebarConfig() {
        routesButton.setVisible(false);
        usersButton.setVisible(false);
        changeLogButton.setVisible(false);
    }

}
