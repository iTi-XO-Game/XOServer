package com.tornado.xoserver;

import com.tornado.xoserver.database.DBInitializer;
import com.tornado.xoserver.server.ResponseManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML(Screen.DASHBOARD));
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("Tic Tac Toe Server");
        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        ResponseManager responseManager = ResponseManager.getInstance();
        responseManager.sendExit();
        Platform.exit();
        System.exit(0);
        super.stop();
    }

    public static void main(String[] args) {
        DBInitializer.init();
        launch();
    }

    }
