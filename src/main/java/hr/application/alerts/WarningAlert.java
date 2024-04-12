package hr.application.alerts;

import javafx.scene.control.Alert;

public class WarningAlert extends BaseAlert {

    public WarningAlert(String headerText, String contentText) {
        super(headerText, contentText);
    }

    @Override
    public Alert createAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        configureAlert(alert);
        return alert;
    }

}
