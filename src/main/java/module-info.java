module com.tornado.xoserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires com.google.gson;


    opens com.tornado.xoserver.controllers to javafx.fxml;
    opens com.tornado.xoserver.models com.google.gson;
    exports com.tornado.xoserver;
}
