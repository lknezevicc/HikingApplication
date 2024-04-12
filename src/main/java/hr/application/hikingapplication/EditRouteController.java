package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.entities.*;
import hr.application.enums.AlertEnums;
import hr.application.scenemanagement.SceneManager;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.TimeConvert;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditRouteController extends EditingAndAddingController {
    private HikingRoute route;
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
    private Button updateRoute;

    public void initialize() {
        route = SceneManager.getInstance().getSharedObject(HikingRoute.class);
        SceneManager.getInstance().clearSharedObject();

        super.configureDurationSpinner(duration);
        super.configureDifficultySlider(difficulty);
        super.configureListView(imagesListView);
        addListeners();
        configureUI();

        imagesListView.setItems(FXCollections.observableList(DatabaseUtility.getImagesForRoute(route.getID())));
    }

    private void addListeners() {
        removeImageButton.disableProperty().bind(imagesListView.getSelectionModel().selectedItemProperty().isNull());
        updateRoute.disableProperty().bind(routeName.textProperty().isEmpty()
                .or(mountainPeak.textProperty().isEmpty())
                .or(startingPoint.textProperty().isEmpty())
                .or(endingPoint.textProperty().isEmpty())
                .or(description.textProperty().isEmpty()));
    }

    private void configureUI() {
        difficulty.setValue(route.getDifficulty());
        duration.getValueFactory().setValue(TimeConvert.convertToHoursAndMinutesString(route.getDuration()));
        length.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 100, route.getLength(), 0.1));
        routeName.setText(route.getName());
        mountainPeak.setText(route.getMountainPeak());
        startingPoint.setText(route.getStartingPoint());
        endingPoint.setText(route.getEndingPoint());
        description.setText(route.getDescription());
    }

    public void updateRoute() {
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

        Optional<ButtonType> alertResult = AlertFactory.getAlert(AlertEnums.CONFIRMATION, "Edit route",
                "Are you sure you want to edit this route?").createAlert().showAndWait();
        if (alertResult.isEmpty() || alertResult.get() != ButtonType.OK) return;

        HikingRoute newRoute = new HikingRoute.Builder()
                .withID(route.getID())
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

        startThreads(newRoute);

        AlertFactory.getAlert(AlertEnums.INFO, "Edit route", "Route updated successfully!").createAlert().show();
        SceneManager.getInstance().setPreviousScene();
    }

    private void startThreads(HikingRoute newRoute) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> DatabaseUtility.editHikingRoute(route, newRoute), executor)
                .thenRun(() -> imagesListView.getItems()
                        .stream()
                        .filter(image -> image.getID() == null)
                        .forEach(image -> DatabaseUtility.updateImagesForRoute(newRoute.getID(), image)))
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
                if (selectedImage.getID() != null)
                    DatabaseUtility.deleteImage(selectedImage);
                imagesListView.getSelectionModel().clearSelection();
            }
        }
    }

}
