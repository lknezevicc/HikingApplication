package hr.application.alerts;

import javafx.scene.control.Alert;

public abstract class BaseAlert implements AlertAction {
    protected String headerText;
    protected String contentText;

    public BaseAlert(String headerText, String contentText) {
        this.headerText = headerText;
        this.contentText = contentText;
    }

    protected void configureAlert(Alert alert) {
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
    }

}
