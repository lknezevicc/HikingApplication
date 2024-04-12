package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.entities.*;
import hr.application.enums.AlertEnums;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.TimeConvert;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRouteController extends EditingAndAddingController {
    @FXML
    private TextField routeName;
    @FXML
    private TextField mountainPeak;
    @FXML
    private TextField startingPoint;
    @FXML
    private TextField endingPoint;
    @FXML
    private Spinner<Double> length;
    @FXML
    private Spinner<String> duration;
    @FXML
    private Slider difficulty;
    @FXML
    private TextArea description;
    @FXML
    private ListView<ImageRepresentation> imagesListView;
    @FXML
    private Button addImageButton;
    @FXML
    private Button removeImageButton;
    @FXML
    private Button addRouteButton;

    public void initialize() {
        super.configureDurationSpinner(duration);
        super.configureDifficultySlider(difficulty);
        super.configureListView(imagesListView);
        configureUI();
    }

    public void addRoute() {
        if (length.getValue().equals(0.0)) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Invalid input!",
                    "Length can't be 0!").createAlert().show();
            return;
        }

        if (duration.getValue().equals("00:00")) {
            AlertFactory.getAlert(AlertEnums.ERROR, "Invalid input!",
                    "Duration can't be 00:00!").createAlert().show();
            return;
        }

        HikingRoute route = new HikingRoute.Builder()
                .withName(routeName.getText())
                .withMountainPeak(mountainPeak.getText())
                .withStartingPoint(startingPoint.getText())
                .withEndingPoint(endingPoint.getText())
                .withLength(length.getValue())
                .withDuration(TimeConvert.convertToMinutes(duration.getValue()))
                .withDifficulty((int) difficulty.getValue())
                .withDescription(description.getText())
                .withAuthor(UserSession.getInstance().getUser().getUsername())
                .build();

        startThreads(route);
        AlertFactory.getAlert(AlertEnums.INFO, "Success!",
                "Route successfully added!").createAlert().show();
        clearInputs();
    }

    private void startThreads(HikingRoute route) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> DatabaseUtility.saveHikingRoute(route), executor)
                .thenRun(() -> DatabaseUtility.saveImagesForRoute(imagesListView.getItems()))
                .thenRun(executor::shutdown);
    }

    public void addImage() {
        super.addImage(addImageButton, imagesListView);
    }

    public void deleteImage() {
        if (!imagesListView.getSelectionModel().isEmpty()) {
            Optional<ButtonType> alertResult = AlertFactory.getAlert(AlertEnums.CONFIRMATION, "Delete image",
                    "Are you sure you want to delete this image?").createAlert().showAndWait();

            if (alertResult.isPresent() && alertResult.get() == ButtonType.OK) {
                ImageRepresentation selectedImage = imagesListView.getSelectionModel().getSelectedItem();
                imagesListView.getItems().remove(selectedImage);
                imagesListView.getSelectionModel().clearSelection();
            }
        }
    }

    private void clearInputs() {
        routeName.clear();
        mountainPeak.clear();
        startingPoint.clear();
        endingPoint.clear();
        description.clear();
        duration.getValueFactory().setValue("00:00");
        length.getValueFactory().setValue(0.0);
        imagesListView.getItems().clear();
    }

    private void configureUI() {
        length.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, 0, 0.1));

        addRouteButton.disableProperty().bind(routeName.textProperty().isEmpty()
                .or(mountainPeak.textProperty().isEmpty())
                .or(startingPoint.textProperty().isEmpty())
                .or(endingPoint.textProperty().isEmpty())
                .or(description.textProperty().isEmpty()));

        removeImageButton.disableProperty().bind(imagesListView.getSelectionModel().selectedItemProperty().isNull());
    }
}
