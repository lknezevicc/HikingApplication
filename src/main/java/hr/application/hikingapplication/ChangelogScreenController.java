package hr.application.hikingapplication;

import hr.application.changemanagement.DataChange;
import hr.application.changemanagement.DataChangeTracker;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class ChangelogScreenController {
    private final DataChangeTracker dataChangeTracker = new DataChangeTracker();
    @FXML
    private ListView<DataChange<?, ?>> changesListView;
    @FXML
    private Button exportButton;

    public void initialize() {
        changesListView.setItems(FXCollections.observableArrayList(dataChangeTracker.getChanges()));
        configureListView();

        exportButton.disableProperty().bind(changesListView.itemsProperty().isNull());
        exportButton.setOnAction(event -> new Thread(dataChangeTracker::export).start());
    }

    private void configureListView() {
        changesListView.setPlaceholder(new Label("No changes found!"));
        changesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DataChange<?, ?> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox objectNameHBox = createHBox(item.getObjectName());
                    HBox oldValueHBox = createHBox(item.getOldValue().toString());
                    HBox newValueHBox = createHBox(item.getNewValue().toString());
                    HBox timestampHBox = createHBox(item.getTimestamp().timestamp());
                    HBox changedByHBox = createHBox(item.getUserRole().name());

                    HBox hbox = new HBox(objectNameHBox, oldValueHBox, newValueHBox, timestampHBox, changedByHBox);

                    setText(null);
                    setGraphic(hbox);
                }
            }
        });
    }

    private HBox createHBox(String text) {
        Label label = new Label(text);
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.setPrefWidth(145);
        hbox.setPadding(new Insets(5));
        return hbox;
    }

}
