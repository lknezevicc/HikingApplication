package hr.application.alerts;

import javafx.scene.control.Alert;

public class ConfirmationAlert extends BaseAlert {
    public ConfirmationAlert(String headerText, String contentText) {
        super(headerText, contentText);
    }

    @Override
    public Alert createAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        configureAlert(alert);
        return alert;
    }
}
