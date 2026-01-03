/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML
    private VBox chartContainer;       

    @FXML
    private ListView<String> logsList;  

    @FXML
    private Button startServerButton;

    @FXML
    private Button stopServerButton;

    @FXML
    private Label serverStatusLabel;

    
    @FXML
    private VBox totalUsersCard, onlineUsersCard, offlineUsersCard, activeSessionsCard;

    @FXML
    private Label totalUsersLabel, onlineUsersLabel, offlineUsersLabel, activeSessionsLabel;

    
    private LineChart<Number, Number> activityChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupChart();      
        loadLogs();        
        setupButtons();   
          setupStatsCards();
    }

    
    private void setupChart() {
      
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Users");

       
        activityChart = new LineChart<>(xAxis, yAxis);
        activityChart.setTitle("Server Activity");
        activityChart.setLegendVisible(false);

       
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().addAll(
            new XYChart.Data<>(1, 20),
            new XYChart.Data<>(2, 60),
            new XYChart.Data<>(3, 30),
            new XYChart.Data<>(4, 80),
            new XYChart.Data<>(5, 50)
        );

        activityChart.getData().add(series);

       
        chartContainer.getChildren().add(activityChart);
        VBox.setVgrow(activityChart, javafx.scene.layout.Priority.ALWAYS);
    
        
    }

    
 
    private void setupStatsCards() {
    totalUsersLabel.setText("150");
    onlineUsersLabel.setText("85");
    offlineUsersLabel.setText("65");
    activeSessionsLabel.setText("40");

    totalUsersCard.setOnMouseClicked(e -> openUserList("Total Users", getAllUsers()));
    onlineUsersCard.setOnMouseClicked(e -> openUserList("Online Users", getOnlineUsers()));
    offlineUsersCard.setOnMouseClicked(e -> openUserList("Offline Users", getOfflineUsers()));
    activeSessionsCard.setOnMouseClicked(e -> openUserList("Active Sessions", getActiveSessions()));
}
    
    
    private void loadLogs() {
        logsList.getItems().addAll(
            "INFO: Server started on port 8080",
            "NET: Listening for connections",
            "GAME: Room created",
            "WARN: Packet loss detected",
            "INFO: New connection received"
        );
    }

    private void setupButtons() {
        startServerButton.setOnAction(e -> startServer());
        stopServerButton.setOnAction(e -> stopServer());
    }

    private void startServer() {
        serverStatusLabel.setText("RUNNING");
        logsList.getItems().add("INFO: Server started successfully.");
    }

    private void stopServer() {
        serverStatusLabel.setText("STOPPED");
        logsList.getItems().add("INFO: Server stopped.");
    }
    
    
    
    private List<String> getAllUsers() { return List.of("User1", "User2", "User3"); }
    private List<String> getOnlineUsers() { return List.of("User1", "User3"); }
    private List<String> getOfflineUsers() { return List.of("User2"); }
    private List<String> getActiveSessions() { return List.of("User1"); }





private void openUserList(String title, List<String> users) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tornado/xoserver/UserListScreen.fxml"));
        Parent root = loader.load();

        UserListController controller = loader.getController();
        controller.setData(title, users);

        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); 
        stage.show();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}
}
