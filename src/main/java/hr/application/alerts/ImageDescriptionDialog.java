package hr.application.alerts;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class ImageDescriptionDialog {
    private final Dialog<String> dialog;
    private String description;

    public ImageDescriptionDialog() {
        dialog = new Dialog<>();
        dialog.setTitle("Image Description");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField descriptionArea = new TextField();
        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionArea, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        dialog.setResultConverter(dialogButton -> {
            if (descriptionArea.getText().isEmpty()) {
                return "No description.";
            } else {
                return descriptionArea.getText();
            }
        });
    }

    public static ImageDescriptionDialog create() {
        return new ImageDescriptionDialog();
    }

    public ImageDescriptionDialog getImageDescription() {
        Optional<String> result = dialog.showAndWait();
        description = result.orElse("No description.");

        return this;
    }

    public String getDescription() {
        return description;
    }
}