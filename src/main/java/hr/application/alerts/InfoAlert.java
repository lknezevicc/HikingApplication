package hr.application.alerts;

import javafx.scene.control.Alert;

public class InfoAlert extends BaseAlert {

    public InfoAlert(String headerText, String contentText) {
        super(headerText, contentText);
    }

    @Override
    public Alert createAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        configureAlert(alert);
        return alert;
    }

}
