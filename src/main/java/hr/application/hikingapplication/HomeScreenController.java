package hr.application.hikingapplication;

import hr.application.entities.HikingRoute;
import hr.application.entities.FilteringController;
import hr.application.utilities.DatabaseUtility;
import hr.application.utilities.TimeConvert;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class HomeScreenController extends FilteringController {
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
    private TableColumn<HikingRoute, String> creatorColumn;

    public void initialize() {
        hikingRoutes = DatabaseUtility.getHikingRoutes();
        hikingRoutesTable.setItems(FXCollections.observableList(hikingRoutes));
        configureUI();
        setListeners();
        hikingRoutesTable.setOnMouseClicked(event -> super.switchToRouteDetails(hikingRoutesTable, event));
    }

    private void configureUI() {
        filterComboBox.setItems(FXCollections.observableList(filterFunctions.keySet().stream().toList()));
        routeNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        mountainPeakColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMountainPeak()));
        durationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(TimeConvert.convertToHoursAndMinutesString(cellData.getValue().getDuration())));
        difficultyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDifficulty().toString() + " / 10"));
        creatorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));

    }

    private void setListeners() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filter(newValue));
        filterComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> filter(searchField.getText()));
    }

    private void filter(String searchValue) {
        super.filter(searchValue, filterComboBox, hikingRoutesTable, hikingRoutes);
    }

}
