package hr.application.entities;

import hr.application.scenemanagement.SceneManager;
import hr.application.scenemanagement.SceneType;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class FilteringController {
    protected final Map<String, Function<HikingRoute, ?>> filterFunctions = Map.of(
            "Name", HikingRoute::getName,
            "Mountain peak", HikingRoute::getMountainPeak,
            "Duration", HikingRoute::getDuration,
            "Difficulty", HikingRoute::getDifficulty
    );

    protected void filter(String searchValue, ComboBox<String> filterComboBox, TableView<HikingRoute> hikingRoutesTable, List<HikingRoute> hikingRoutes) {
        String filterOption = filterComboBox.getSelectionModel().getSelectedItem();
        if (filterOption != null) {
            Function<HikingRoute, ?> filterFunction = filterFunctions.get(filterOption);
            Comparator<HikingRoute> comparator = filterFunction.apply(hikingRoutes.getFirst()) instanceof Integer ?
                    Comparator.comparing(route -> (Integer) filterFunction.apply(route)) :
                    Comparator.comparing(route -> filterFunction.apply(route).toString());

            hikingRoutesTable.setItems(FXCollections.observableList(
                    hikingRoutes.stream()
                            .filter(route -> route.getName().toLowerCase().contains(searchValue.toLowerCase()))
                            .sorted(comparator)
                            .toList()));
        } else {
            hikingRoutesTable.setItems(FXCollections.observableList(
                    hikingRoutes.stream()
                            .filter(route -> route.getName().toLowerCase().contains(searchValue.toLowerCase()))
                            .toList()));
        }
    }

    protected void switchToRouteDetails(TableView<HikingRoute> hikingRoutesTable, MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !hikingRoutesTable.getSelectionModel().isEmpty()) {
            HikingRoute selectedRoute = hikingRoutesTable.getSelectionModel().getSelectedItem();
            SceneManager.getInstance().setSharedObject(selectedRoute);
            SceneManager.getInstance().setScene(SceneType.ROUTE_DETAILS);
        }
    }

    protected void switchToEditRoute(TableView<HikingRoute> hikingRoutesTable) {
        if (!hikingRoutesTable.getSelectionModel().isEmpty()) {
            HikingRoute selectedRoute = hikingRoutesTable.getSelectionModel().getSelectedItem();
            SceneManager.getInstance().setSharedObject(selectedRoute);
            SceneManager.getInstance().setScene(SceneType.EDIT_ROUTE);
        }
    }
}
