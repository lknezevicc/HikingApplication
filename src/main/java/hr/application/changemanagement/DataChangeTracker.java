package hr.application.changemanagement;

import hr.application.exceptions.SerializationFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class DataChangeTracker implements DataChangeTrackerAction {
    public static final Logger logger = LoggerFactory.getLogger(DataChangeTracker.class);
    private static final String SERIALIZATION_PATH = "dat/changes.ser";
    private static final String XLSX_OUTPUT_PATH = "dat/changes.xlsx";
    private static final Object lock = new Object();
    private static List<DataChange<?, ?>> changes;

    public DataChangeTracker() {
        synchronized (lock) {
            changes = readChangesFromFile();
        }
    }

    @Override
    public void writeChangeToFile(DataChange<?, ?> dataChange) {
        if (dataChange == null) return;
        if (!ensureSerializationFileExists()) {
            logger.error("Serialization file does not exist, cannot write data change to file");
            return;
        }

        synchronized (lock) {
            changes.add(dataChange);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_PATH))) {
                oos.writeObject(changes);
                logger.info("Data change for {} written to file!", dataChange.getObjectName());
            } catch (IOException e) {
                logger.error("Error while writing data change to file, [{}]", e.getMessage());
            }
        }
    }

    @Override @SuppressWarnings("unchecked")
    public List<DataChange<?, ?>> readChangesFromFile() {
        if (!ensureSerializationFileExists()) {
            logger.error("Serialization file does not exist, cannot read data changes from file");
            return new ArrayList<>();
        }

        File file = new File(SERIALIZATION_PATH);
        if (file.length() == 0) return new ArrayList<>();

        synchronized (lock) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SERIALIZATION_PATH))) {
                changes = (List<DataChange<?, ?>>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Error while reading data change from file, [{}]", e.getMessage());
            }
        }
        return new ArrayList<>(changes);
    }

    @Override
    public void export() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Changes");

            CellStyle cs = workbook.createCellStyle();
            cs.setWrapText(true);
            cs.setVerticalAlignment(VerticalAlignment.TOP);

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);

            headerRow.createCell(0).setCellValue("Object Name");
            headerRow.createCell(1).setCellValue("Old Value");
            headerRow.createCell(2).setCellValue("New Value");
            headerRow.createCell(3).setCellValue("Timestamp");
            headerRow.createCell(4).setCellValue("Changed By");

            synchronized (lock) {
                for (DataChange<?, ?> change : changes) {
                    Row row = sheet.createRow(rowNum++);
                    Cell cell;

                    cell = row.createCell(0);
                    cell.setCellValue(change.getObjectName());
                    cell.setCellStyle(cs);

                    cell = row.createCell(1);
                    cell.setCellValue(change.getOldValue().toString());
                    cell.setCellStyle(cs);

                    cell = row.createCell(2);
                    cell.setCellValue(change.getNewValue().toString());
                    cell.setCellStyle(cs);

                    cell = row.createCell(3);
                    cell.setCellValue(change.getTimestamp().timestamp());
                    cell.setCellStyle(cs);

                    cell = row.createCell(4);
                    cell.setCellValue(change.getUserRole().name());
                    cell.setCellStyle(cs);
                }
            }

            int widthUnits = 256*40;
            for(int columnIndex = 0; columnIndex < 5; columnIndex++) {
                sheet.setColumnWidth(columnIndex, widthUnits);
            }

            FileOutputStream outputStream = new FileOutputStream(XLSX_OUTPUT_PATH);
            workbook.write(outputStream);
            logger.info("Changes exported to file successfully in dat/ folder!");

        } catch (IOException e) {
            logger.error("Error while exporting changes to file, [{}]", e.getMessage());
        }
    }

    private boolean ensureSerializationFileExists() {
        Path path = Paths.get(SERIALIZATION_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new SerializationFileException("Error while creating serialization file: " + e.getMessage());
            }
        }

        return Files.exists(path);
    }

    public List<DataChange<?, ?>> getChanges() {
        synchronized (lock) {
            return new ArrayList<>(changes);
        }
    }

}
