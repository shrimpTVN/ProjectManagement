module com.app.src {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires java.sql;
    requires java.logging;

    requires mysql.connector.j;

    requires org.pf4j;
    requires java.management;
    requires java.desktop;
    requires com.google.gson;
    opens com.app.src to javafx.fxml;
    exports com.app.src;
    exports com.app.src.utils;
    opens com.app.src.utils to javafx.fxml;
    exports com.app.src.controllers;
    opens com.app.src.controllers to javafx.fxml;
    exports com.app.src.core;
    opens com.app.src.core to javafx.fxml;
    exports com.app.src.api;
    opens com.app.src.api to javafx.fxml;
    opens com.app.src.models to javafx.base, javafx.fxml, com.google.gson;
    exports com.app.src.controllers.project;
    opens com.app.src.controllers.project to javafx.fxml;
    exports com.app.src.controllers.task;
    opens com.app.src.controllers.task to javafx.fxml;
    exports com.app.src.models;
    opens com.app.src.controllers.notification to javafx.fxml;
    exports com.app.src.authentication;
    opens com.app.src.authentication to javafx.fxml;
}