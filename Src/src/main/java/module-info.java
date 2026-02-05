module com.app.src {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;

    opens com.app.src to javafx.fxml;
    exports com.app.src;
    exports com.app.src.utils;
    opens com.app.src.utils to javafx.fxml;
    exports com.app.src.controllers;
    opens com.app.src.controllers to javafx.fxml;
}