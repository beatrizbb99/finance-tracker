module com.finance {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.sql;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome;
    requires javafx.graphics;

    exports com.finance;
    exports com.finance.controller;
    exports com.finance.model;
    exports com.finance.database;
    exports com.finance.util;

    opens com.finance to javafx.fxml;
    opens com.finance.controller to javafx.fxml;
}
