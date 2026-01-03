/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver.controllers;

import com.tornado.xoserver.server.ServerManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author lenovo
 */
public class ServerHomeController implements Initializable {

    @FXML
    private Label serverStatus;
    @FXML
    private Button startServerButton;
    @FXML
    private Button stopServerButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        serverStatus.setText("Server : Off");
    }

    @FXML
    private void startServer(ActionEvent event) {
        serverStatus.setText("Starting server");
        ServerManager sManager = ServerManager.getInstance();
        sManager.startServer(() -> {
            Platform.runLater(() -> {
                serverStatus.setText("Server : On");
            });
        });
    }

    @FXML
    private void stopServer(ActionEvent event) {
        serverStatus.setText("Stopping server");
        ServerManager sManager = ServerManager.getInstance();
        sManager.stopServer(() -> {
            Platform.runLater(() -> {
                serverStatus.setText("Server : Off");
            });
        });
    }
}
