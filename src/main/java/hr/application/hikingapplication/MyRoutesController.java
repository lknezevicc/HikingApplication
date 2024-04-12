package hr.application.hikingapplication;

import hr.application.alerts.AlertFactory;
import hr.application.entities.HikingRoute;
import hr.application.entities.FilteringController;
import hr.application.enums.AlertEnums;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.TimeConvert;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyRoutesController extends FilteringController {
    private List<HikingRoute> hikingRoutes;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TableView<HikingRoute> hikingRoutesTable;
    @FXML
    private TableColumn<HikingRoute, String> routeNameColumn;
    @FXML
    private TableColumn<HikingRoute, String> mountainPeakColumn;
    @FXML
    private TableColumn<HikingRoute, String> durationColumn;
    @FXML
    private TableColumn<HikingRoute, String> difficultyColumn;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    public void initialize() {
        hikingRoutes = DatabaseUtility.getHikingRoutesByUsername();
        hikingRoutesTable.setItems(FXCollections.observableList(hikingRoutes));
        configureUI();
        addListeners();
        hikingRoutesTable.setOnMouseClicked(event -> super.switchToRouteDetails(hikingRoutesTable, event));
    }

    public void editRoute() {
        super.switchToEditRoute(hikingRoutesTable);
    }

    public void deleteRoute() {
        if (!hikingRoutesTable.getSelectionModel().isEmpty()) {
            Optional<ButtonType> result = AlertFactory.getAlert(AlertEnums.CONFIRMATION, "Delete route",
                    "Are you sure you want to delete this route?").createAlert().showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) startThreads();
        }
    }

    private void startThreads() {
        HikingRoute selectedRoute = hikingRoutesTable.getSelectionModel().getSelectedItem();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        CompletableFuture.runAsync(() -> DatabaseUtility.deleteHikingRoute(selectedRoute), executorService)
                .thenRun(() -> Platform.runLater(this::updateTable))
                .thenRun(executorService::shutdown);
    }

    private void updateTable() {
        hikingRoutes = DatabaseUtility.getHikingRoutesByUsername();
        hikingRoutesTable.setItems(FXCollections.observableList(hikingRoutes));
    }

    private void filter(String searchValue) {
        super.filter(searchValue, filterComboBox, hikingRoutesTable, hikingRoutes);
    }

    private void configureUI() {
        filterComboBox.setItems(FXCollections.observableList(filterFunctions.keySet().stream().toList()));
        routeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        mountainPeakColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMountainPeak()));
        durationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(TimeConvert.convertToHoursAndMinutesString(cellData.getValue().getDuration())));
        difficultyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty().toString() + " / 10"));
    }

    private void addListeners() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filter(newValue));
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(searchField.getText()));

        editButton.disableProperty().bind(hikingRoutesTable.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty().bind(hikingRoutesTable.getSelectionModel().selectedItemProperty().isNull());
    }

}
