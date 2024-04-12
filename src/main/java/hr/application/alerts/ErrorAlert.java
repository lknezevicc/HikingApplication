package hr.application.alerts;

import javafx.scene.control.Alert;

public class ErrorAlert extends BaseAlert {
    public ErrorAlert(String headerText, String contentText) {
        super(headerText, contentText);
    }

    @Override
    public Alert createAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        configureAlert(alert);
        return alert;
    }

}
