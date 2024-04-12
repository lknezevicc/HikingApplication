package hr.application.entities;

import hr.application.alerts.AlertFactory;
import hr.application.alerts.ImageDescriptionDialog;
import hr.application.enums.AlertEnums;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Optional;
import java.util.stream.IntStream;

public abstract class EditingAndAddingController {
    protected void configureListView(ListView<ImageRepresentation> listView) {
        listView.setPlaceholder(new Label("Add images for route image gallery!"));
        listView.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final VBox vBox = new VBox();
            private final Label label = new Label();

            {
                imageView.setFitHeight(220);
                imageView.setFitWidth(350);
                vBox.setSpacing(10);
                vBox.setPadding(new Insets(10));
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(ImageRepresentation item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(new Image(item.getImageFile().toURI().toString()));
                    label.setText(item.getDescription());
                    setGraphic(vBox);
                }
            }
        });
    }

    protected void addImage(Button addImageButton, ListView<ImageRepresentation> imagesListView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        Optional<File> imageFile = Optional.ofNullable(fileChooser.showOpenDialog(addImageButton.getScene().getWindow()));
        imageFile.ifPresent(file -> {
            if (imagesListView.getItems().stream().anyMatch(image -> image.getImageFile().equals(file))) {
                AlertFactory.getAlert(AlertEnums.WARNING, "Image already added!",
                        "You have already added this image!").createAlert().show();
                return;
            }

            String description = ImageDescriptionDialog.create().getImageDescription().getDescription();
            ImageRepresentation imageRepresentation = new ImageRepresentation(file, description);
            imagesListView.getItems().add(imageRepresentation);
        });
    }

    protected void configureDifficultySlider(Slider difficulty) {
        difficulty.setMin(1);
        difficulty.setMax(10);
        difficulty.setValue(5);
        difficulty.setShowTickLabels(true);
        difficulty.setSnapToTicks(true);
        difficulty.setMajorTickUnit(1);
        difficulty.setMinorTickCount(0);
    }

    protected void configureDurationSpinner(Spinner<String> duration) {
        duration.setValueFactory(new SpinnerValueFactory.ListSpinnerValueFactory<>(
                FXCollections.observableList(IntStream.range(0, 24*6 + 1) // 24 sata * 6 intervala po 10 minuta + 1 za 24:00
                        .mapToObj(i -> {
                            int hours = i / 6;
                            int minutes = (i % 6) * 10;
                            if (hours == 24) minutes = 0; // minute na 0 za 24:00
                            return String.format("%02d:%02d", hours, minutes);
                        }).toList())));
    }
}
