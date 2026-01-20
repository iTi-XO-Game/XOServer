/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver.controllers;

import com.tornado.xoserver.App;
import com.tornado.xoserver.Screen;
import com.tornado.xoserver.database.PlayerDAO;
import com.tornado.xoserver.models.Player;
import com.tornado.xoserver.server.ServerLog;
import com.tornado.xoserver.server.ResponseManager;
import com.tornado.xoserver.server.ServerManager;

import java.io.IOException;

import com.tornado.xoserver.server.ServerStateManager;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    private final Map<Integer, Player> allPlayers = new ConcurrentHashMap<>();
    private final Map<Integer, Player> offlinePlayers = new ConcurrentHashMap<>();
    private final AtomicInteger onlineCount = new AtomicInteger(0);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupChart();
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
        x.setLabel("Time (seconds)");

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
        onlineUsersSeries.getData().add(
                new XYChart.Data<>(0, 0)
        );
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    minuteCounter++;
                    onlineUsersSeries.getData().add(
                            new XYChart.Data<>(minuteCounter, onlineCount.get())
                    );
                    if (onlineUsersSeries.getData().size() > 60) {
                        onlineUsersSeries.getData().removeFirst();
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void setupStats() {
        List<Player> players = PlayerDAO.getInstance().getAllPlayers();
        if (players == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "انت ليه عايز تفتح سيرفرين في نفس الوقت؟\nاقفل السيرفر المفتوح و تعالى تاني", ButtonType.OK);
            alert.setHeaderText("احنا هنهزر");
            alert.showAndWait();
            Platform.exit();
            System.exit(0);
        }

        players.forEach(p -> {
            allPlayers.put(p.getId(), p);
            offlinePlayers.put(p.getId(), p);
        });
        updateCount();
        registerOnlinePlayersObserver();
        registerDAOListener();
    }

    private void updateCount() {
        Platform.runLater(() -> {
            totalUsersLabel.setText(allPlayers.size() + "");
            offlineUsersLabel.setText(offlinePlayers.size() + "");
            onlineUsersLabel.setText(onlineCount.get() + "");
        });
    }

    private void registerOnlinePlayersObserver() {
        ServerStateManager.getInstance().addOnlinePlayersObserver((player, action) -> {
            switch (action) {
                case ADDED -> {
                    onlineCount.incrementAndGet();
                    offlinePlayers.remove(player.getId());
                    updateCount();
                }
                case REMOVED -> {
                    onlineCount.decrementAndGet();
                    offlinePlayers.put(player.getId(), player);
                    updateCount();
                }
                case UPDATED -> {
                    allPlayers.put(player.getId(), player);
                }
                case CLEARED -> {
                    offlinePlayers.putAll(allPlayers);
                    onlineCount.set(0);
                    updateCount();
                }
            }
        });
    }

    private void registerDAOListener() {
        PlayerDAO.getInstance().registerDAOListener((player, action) -> {
            switch (action) {
                case ADDED -> {
                    allPlayers.put(player.getId(), player);
                    offlinePlayers.put(player.getId(), player);
                }
                case UPDATED -> {
                    allPlayers.put(player.getId(), player);
                }
            }
            updateCount();
        });
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
        ResponseManager responseManager = ResponseManager.getInstance();
        responseManager.sendExit();
        sManager.stopServer(() -> {
            Platform.runLater(() -> {
                serverStatusLabel.setText("STOPPED");
                logsList.getItems().add("INFO: Server stopped");
            });
        });
    }

    @FXML
    private void onTotalUsersClick(MouseEvent event) {
        List<String> names = allPlayers.values().stream().map(Player::getUsername).toList();
        openUsers("Total Users", names);
    }

    @FXML
    private void onOnlineUsersClick(MouseEvent event) {
        List<String> players = ServerStateManager.getInstance().getOnlinePlayers().stream().map(Player::getUsername).toList();
        openUsers("Online Users", players);
    }

    @FXML
    private void onOfflineUsersClick(MouseEvent event) {
        List<String> players = offlinePlayers.values().stream().map(Player::getUsername).toList();
        openUsers("Offline Users", players);
    }

}
