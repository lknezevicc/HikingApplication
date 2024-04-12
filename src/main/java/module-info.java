module hr.application.hikingapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires bcrypt;
    requires org.slf4j;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens hr.application.hikingapplication to javafx.fxml;
    exports hr.application.hikingapplication;
}