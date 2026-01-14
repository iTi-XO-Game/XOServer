/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver.controllers;

import com.tornado.xoserver.App;
import com.tornado.xoserver.Screen;
import com.tornado.xoserver.server.ServerLog;
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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javafx.scene.layout.BorderPane;

public class DashboardController implements Initializable {

    @FXML
    private VBox chartContainer;
    @FXML
    private ListView<String> logsList;
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox totalUsersCard, onlineUsersCard, offlineUsersCard;
    @FXML
    private Label totalUsersLabel, onlineUsersLabel, offlineUsersLabel;
    @FXML
    private Label serverStatusLabel;
    @FXML
    private Button startServerButton, stopServerButton;

    private XYChart.Series<Number, Number> onlineUsersSeries;
    private int minuteCounter = 0;

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField socketTextField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupChart();
        addInitialChartPoint();
        setupStats();
        startOnlineUsersChartUpdater();
        ServerLog.setUiConsumer(log
                -> Platform.runLater(() -> {
                    logsList.getItems().add(log);
                    logsList.scrollTo(logsList.getItems().size() - 1);

                    if (logsList.getItems().size() > 200) {
                        logsList.getItems().removeFirst();
                    }
                })
        );

        ServerManager.getInstance().startServer(() -> {
        });

        socketTextField.setText("8181");
        Platform.runLater(() -> rootPane.requestFocus());
        try {

            InetAddress localhost = InetAddress.getLocalHost();
            String ip = localhost.getHostAddress();

            ipTextField.setText(ip);

        } catch (UnknownHostException e) {
            ipTextField.setText("127.0.0.1"); // the default...
            //e.printStackTrace();
        }
    }

    private void setupChart() {
        NumberAxis x = new NumberAxis();
        x.setLabel("Time (minutes)");

        NumberAxis y = new NumberAxis();
        y.setLabel("Online Users");

        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.setCreateSymbols(false);
        chart.setLegendVisible(false);

        onlineUsersSeries = new XYChart.Series<>();

        chart.getData().add(onlineUsersSeries);
        chartContainer.getChildren().add(chart);
        VBox.setVgrow(chart, javafx.scene.layout.Priority.ALWAYS);
    }

    private void startOnlineUsersChartUpdater() {
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    int onlineCount
                            = ServerManager.getInstance().getOnlineUsersCount();

                    minuteCounter++;

                    onlineUsersSeries.getData().add(
                            new XYChart.Data<>(minuteCounter, onlineCount)
                    );

                    if (onlineUsersSeries.getData().size() > 60) {
                        onlineUsersSeries.getData().removeFirst();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void addInitialChartPoint() {
        int online
                = ServerManager.getInstance().getOnlineUsersCount();

        onlineUsersSeries.getData().add(
                new XYChart.Data<>(0, online)
        );
    }

    private void setupStats() {
        getAllPlayers();

        if (Stats.allPlayers == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "انت ليه عايز تفتح سيرفرين في نفس الوقت؟\nاقفل السيرفر المفتوح و تعالى تاني", ButtonType.OK);
            alert.setHeaderText("احنا هنهزر");
            alert.showAndWait().ifPresent((response) -> {
                Platform.exit();
            });

        } else {
            getOnlinePlayers();
            getOfflinePlayers();
            Stats.total.set(Stats.allPlayers.size());
            totalUsersLabel.textProperty().bind(Stats.total.asString());

            Stats.online.set(Stats.allOnlinePlayers.size());
            onlineUsersLabel.textProperty().bind(Stats.online.asString());

            Stats.offline.set(Stats.allOfflinePlayers.size());
            offlineUsersLabel.textProperty().bind(Stats.offline.asString());
        }

    }

    private void getAllPlayers() {
        PlayerDAO playerDAO = new PlayerDAO();
        Stats.allPlayers = playerDAO.getAllPlayersNames();
    }

    private void getOnlinePlayers() {
        ResponseManager manager = ResponseManager.getInstance();
        Stats.allOnlinePlayers = manager.getOnlinePlayersName();
    }

    private void getOfflinePlayers() {
        List<String> temp = new ArrayList<>();

        for (String val : Stats.allPlayers) {
            if (!Stats.allOnlinePlayers.contains(val)) {
                temp.add(val);
            }
        }

        Stats.allOfflinePlayers = temp;
    }

    private void openUsers(String title, List<String> users) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    App.class.getResource(Screen.USER_LIST_SCREEN + ".fxml"));
            Parent root = loader.load();

            UserListController controller = loader.getController();
            controller.setData(title, users);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException ex) {
            //ex.printStackTrace();
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

}
