/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver.controllers;

import com.tornado.xoserver.App;
import com.tornado.xoserver.Screen;
import com.tornado.xoserver.server.ServerLog;
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
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

    private XYChart.Series<Number, Number> onlineUsersSeries;
    private int minuteCounter = 0;

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
                        logsList.getItems().remove(0);
                    }
                })
        );

        ServerManager.getInstance().startServer(() -> {
        });
    }

    private void setupChart() {
        NumberAxis x = new NumberAxis();
        x.setLabel("Time (minutes)");

        NumberAxis y = new NumberAxis();
        y.setLabel("Online Users");

        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.setCreateSymbols(true);
        chart.setLegendVisible(false);

        onlineUsersSeries = new XYChart.Series<>();

        chart.getData().add(onlineUsersSeries);
        chartContainer.getChildren().add(chart);
        VBox.setVgrow(chart, javafx.scene.layout.Priority.ALWAYS);
    }

    private void startOnlineUsersChartUpdater() {
        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.minutes(1), e -> {
                    int onlineCount
                            = ServerManager.getInstance().getOnlineUsersCount();

                    minuteCounter++;

                    onlineUsersSeries.getData().add(
                            new XYChart.Data<>(minuteCounter, onlineCount)
                    );

                    if (onlineUsersSeries.getData().size() > 60) {
                        onlineUsersSeries.getData().remove(0);
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
        totalUsersLabel.setText("1240");
        onlineUsersLabel.setText("42");
        offlineUsersLabel.setText("1198");
        activeSessionsLabel.setText("8");
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
        return List.of("Alice", "Bob", "John");
    }

    private List<String> getOnlineUsers() {
        return List.of("Alice", "John");
    }

    private List<String> getOfflineUsers() {
        return List.of("Bob");
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
