module com.tornado.xoserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.tornado.xoserver to javafx.fxml;
    exports com.tornado.xoserver;
}
