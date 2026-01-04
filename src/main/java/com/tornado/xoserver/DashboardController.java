/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.tornado.xoserver;

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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

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
    @FXML
    private ImageView playIcon;
    @FXML
    private ImageView stopIcon;
    @FXML
    private ImageView usersIcon;

    @FXML
    private ImageView onlineIcon;

    @FXML
    private ImageView offlineIcon;

    @FXML
    private ImageView sessionsIcon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playIcon.setEffect(new ColorAdjust(0, 0, 1, 0));
        stopIcon.setEffect(new ColorAdjust(0, 0, 1, 0));
        makeRounded(usersIcon, 20);
        makeRounded(onlineIcon, 20);
        makeRounded(offlineIcon, 20);
        makeRounded(sessionsIcon, 20);
        setupChart();
        loadLogs();
        setupStats();
        setupActions();
    }

    private void makeRounded(ImageView imageView, double radius) {
        Rectangle clip = new Rectangle(
                imageView.getFitWidth(),
                imageView.getFitHeight()
        );
        clip.setArcWidth(radius);
        clip.setArcHeight(radius);
        imageView.setClip(clip);
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
        totalUsersLabel.setText("1240");
        onlineUsersLabel.setText("42");
        offlineUsersLabel.setText("1198");
        activeSessionsLabel.setText("8");

        totalUsersCard.setOnMouseClicked(e
                -> openUsers("Total Users", getAllUsers()));
        onlineUsersCard.setOnMouseClicked(e
                -> openUsers("Online Users", getOnlineUsers()));
        offlineUsersCard.setOnMouseClicked(e
                -> openUsers("Offline Users", getOfflineUsers()));
    }

    private void setupActions() {
        startServerButton.setOnAction(e -> {
            serverStatusLabel.setText("RUNNING");
            logsList.getItems().add("INFO: Server started");
        });

        stopServerButton.setOnAction(e -> {
            serverStatusLabel.setText("STOPPED");
            logsList.getItems().add("INFO: Server stopped");
        });
    }

    private void openUsers(String title, List<String> users) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("UserListScreen.fxml"));
            Parent root = loader.load();

            UserListController controller = loader.getController();
            controller.setData(title, users);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
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

    private void loadLogs() {
        logsList.getItems().addAll(
                "INFO: Server started",
                "NET: Listening on port 8080",
                "GAME: Room created",
                "WARN: Packet loss detected"
        );
    }

}
