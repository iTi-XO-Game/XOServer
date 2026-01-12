/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver.controllers;

import com.tornado.xoserver.App;
import com.tornado.xoserver.Screen;
import com.tornado.xoserver.database.PlayerDAO;
import com.tornado.xoserver.models.Stats;
import com.tornado.xoserver.server.ResponseManager;
import com.tornado.xoserver.server.ServerManager;
import java.io.IOException;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public class DashboardController implements Initializable {

    @FXML
    private VBox chartContainer;
    @FXML
    private ListView<String> logsList;

    @FXML
    private VBox totalUsersCard, onlineUsersCard, offlineUsersCard, activeSessionsCard;
    @FXML
    private Label totalUsersLabel, onlineUsersLabel, offlineUsersLabel, activeSessionsLabel;
    @FXML
    private Label serverStatusLabel;
    @FXML
    private Button startServerButton, stopServerButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupChart();
        loadLogs();
        setupStats();
        ServerManager.getInstance().startServer(()->{});
    }

    private void setupChart() {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();

        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().addAll(
                new XYChart.Data<>(1, 20),
                new XYChart.Data<>(2, 40),
                new XYChart.Data<>(3, 30),
                new XYChart.Data<>(4, 60),
                new XYChart.Data<>(5, 45)
        );

        chart.getData().add(series);
        chartContainer.getChildren().add(chart);
        VBox.setVgrow(chart, javafx.scene.layout.Priority.ALWAYS);
    }

    private void setupStats() {

        getAllPlayers();
        getOnlinePlayers();
        getOfflinePlayers();

        if(Stats.allPlayers==null || Stats.allOnlinePlayers == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "افتح الداتا بيز يا عسل", ButtonType.OK);
            alert.setHeaderText("احنا هنهزر");
            alert.showAndWait().ifPresent((response) -> {
                Platform.exit();
            });
            
        }
        else {

            Stats.total.set(Stats.allPlayers.size());
            totalUsersLabel.textProperty().bind(Stats.total.asString());

            Stats.online.set(Stats.allOnlinePlayers.size());
            onlineUsersLabel.textProperty().bind(Stats.online.asString());

            Stats.offline.set(Stats.allOfflinePlayers.size());
            offlineUsersLabel.textProperty().bind(Stats.offline.asString());

            activeSessionsLabel.setText("80");
        }

    }

    private void getAllPlayers()
    {
        PlayerDAO playerDAO=new PlayerDAO();
        Stats.allPlayers=playerDAO.getAllPlayersNames();
    }

    private void getOnlinePlayers()
    {
        ResponseManager manager = ResponseManager.getInstance();
        Stats.allOnlinePlayers = manager.getOnlinePlayersName();
    }

    private void getOfflinePlayers()
    {
        List<String> temp = new ArrayList<>();

        for (String val : Stats.allPlayers)
        {
            if (!Stats.allOnlinePlayers.contains(val))
                temp.add(val);
        }

        Stats.allOfflinePlayers = temp;
    }

    private void openUsers(String title, List<String> users) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource(Screen.USER_LIST_SCREEN +".fxml"));
            Parent root = loader.load();

            UserListController controller = loader.getController();
            controller.setData(title, users);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private List<String> getAllUsers() {
        return Stats.allPlayers;
    }

    private List<String> getOnlineUsers() {
        return Stats.allOnlinePlayers;
    }

    private List<String> getOfflineUsers() {
        return Stats.allOfflinePlayers;
    }

    private void loadLogs() {
        logsList.getItems().addAll(
                "INFO: Server started",
                "NET: Listening on port 8080",
                "GAME: Room created",
                "WARN: Packet loss detected"
        );
    }

    @FXML
    private void onStartClick(ActionEvent event) {
        
        ServerManager sManager = ServerManager.getInstance();
        sManager.startServer(() -> {
            Platform.runLater(() -> {
                serverStatusLabel.setText("RUNNING");
                logsList.getItems().add("INFO: Server started");
            });
        });
    }

    @FXML
    private void onStopClick(ActionEvent event) {
        ServerManager sManager = ServerManager.getInstance();
        sManager.stopServer(() -> {
            Platform.runLater(() -> {
                serverStatusLabel.setText("STOPPED");
                logsList.getItems().add("INFO: Server stopped");
            });
        });
    }

    @FXML
    private void onTotalUsersClick(MouseEvent event) {
        openUsers("Total Users", getAllUsers());
    }

    @FXML
    private void onOnlineUsersClick(MouseEvent event) {
        openUsers("Online Users", getOnlineUsers());
    }

    @FXML
    private void onOfflineUsersClick(MouseEvent event) {
        openUsers("Offline Users", getOfflineUsers());
    }

    @FXML
    private void onActiveSessionsClick(MouseEvent event) {
    }

}
