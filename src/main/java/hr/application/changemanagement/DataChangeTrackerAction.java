package hr.application.changemanagement;

import java.util.List;

public sealed interface DataChangeTrackerAction permits DataChangeTracker {
    void writeChangeToFile(DataChange<?, ?> dataChange);
    List<DataChange<?, ?>> readChangesFromFile();
    void export();
}
