module com.tornado.xoserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.desktop;

    opens com.tornado.xoserver to javafx.fxml;
    exports com.tornado.xoserver;
}
