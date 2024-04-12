package hr.application.alerts;

import hr.application.enums.AlertEnums;

public class AlertFactory {
    public static BaseAlert getAlert(AlertEnums type, String headerText, String contentText) {
        return switch (type) {
            case ERROR -> new ErrorAlert(headerText, contentText);
            case WARNING -> new WarningAlert(headerText, contentText);
            case INFO -> new InfoAlert(headerText, contentText);
            case CONFIRMATION -> new ConfirmationAlert(headerText, contentText);
        };
    }

}
