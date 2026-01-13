package com.tornado.xoserver.controllers;

import com.tornado.xoserver.database.GameHistoryDAO;
import com.tornado.xoserver.database.PlayerDAO;
import com.tornado.xoserver.models.GameHistory;
import com.tornado.xoserver.models.Player;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UserListController {

    @FXML
    private ListView<String> usersList;
    @FXML
    private Label titleLabel;

    @FXML
    private Label selectedUserLabel, winsLabel, drawsLabel, lossesLabel;
    @FXML
    private VBox gameRowsContainer;
    @FXML
    private TextField searchTextField;

    private final PlayerDAO playerDAO = new PlayerDAO();

    private int id;

    private List<String> allUsers;
    private List<String> searchUsers;
    Map<Integer, String> opponentNames;
    List<GameHistory> gameModels = new ArrayList<>();

    @FXML
    public void initialize() {
        searchUsers = new ArrayList<>();
        allUsers = new ArrayList<>();
        usersList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldUser, newUser) -> {
                    if (newUser != null) {
                        loadPlayerStats(newUser);
                        getOpponentsUserName();

                        displayGames();
                    }
                });

    }

    @FXML
    void onTextChange(KeyEvent event) {
        searchUsers.clear();
        String textFieldText = searchTextField.getText();
        if (textFieldText.isBlank()) {
            usersList.getItems().setAll(allUsers);
        } else {
            for (String user : allUsers) {
                if (user.contains(textFieldText)) {
                    searchUsers.add(user);
                }
            }
            usersList.getItems().setAll(searchUsers);
        }

    }

    public void setData(String title, List<String> users) {
        allUsers.addAll(users);
        usersList.getItems().setAll(users);
    }

    private void loadPlayerStats(String username) {
        selectedUserLabel.setText(username);

        Player player = playerDAO.getPlayerByUsername(username);
        id = player.getId();

        winsLabel.setText(String.valueOf(player.getWins()));
        drawsLabel.setText(String.valueOf(player.getDraws()));
        lossesLabel.setText(String.valueOf(player.getLosses()));
    }

    public void displayGames() {

        gameRowsContainer.getChildren().clear();

        GameHistoryDAO gameHistoryDAO = new GameHistoryDAO();
        List<GameHistory> gameModels = gameHistoryDAO.getPlayerGames(id);

        for (GameHistory game : gameModels) {
            HBox row = createGameRow(game);
            gameRowsContainer.getChildren().add(row);
        }
    }

    private HBox createGameRow(GameHistory game) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPrefHeight(60);
        row.setPadding(new Insets(0, 20, 0, 20));
        row.setStyle("-fx-border-color: #F1F5F9; -fx-border-width: 0 0 1 0;");

        Label resultLabel = new Label();
        HBox resultContainer = new HBox(resultLabel);
        resultContainer.setAlignment(Pos.CENTER_LEFT);

        resultContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(resultContainer, Priority.ALWAYS);

        if (game.getWinnerId() == null) {
            setupStatusLabel(resultLabel, "Draw", "#F1F5F9", "#64748B");

        } else if (game.getWinnerId() == id) {
            setupStatusLabel(resultLabel, "Victory", "#E6F9ED", "#2ECC71");

        } else {
            setupStatusLabel(resultLabel, "Defeat", "#FEE2E2", "#EF4444");
        }

        int opponentId = game.getPlayerXId() == id ? game.getPlayerOId() : game.getPlayerXId();
        Label opponentLabel = new Label(opponentNames.get(opponentId));
        long time = game.getGameDate();
        LocalDateTime dateTime = Instant
                .ofEpochMilli(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        Label dateLabel = new Label(dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy h:mm a")));

        resultContainer.prefWidthProperty().bind(row.widthProperty().divide(4));
        opponentLabel.prefWidthProperty().bind(row.widthProperty().divide(4));
        dateLabel.prefWidthProperty().bind(row.widthProperty().divide(4));

        opponentLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(opponentLabel, Priority.ALWAYS);
        dateLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(dateLabel, Priority.ALWAYS);

        row.getChildren().addAll(resultContainer, opponentLabel, dateLabel);

        return row;
    }

    private void getOpponentsUserName() {
        opponentNames = new HashMap<>();
        opponentNames = playerDAO.getUsernames(getOpponentIds());
    }

    private List<Integer> getOpponentIds() {
        List<Integer> temp = new ArrayList<>();
        GameHistoryDAO gameHistoryDAO = new GameHistoryDAO();
        gameModels = gameHistoryDAO.getPlayerGames(id);
        for (GameHistory game : gameModels) {
            int opponentId = game.getPlayerXId() == id ? game.getPlayerOId() : game.getPlayerXId();
            temp.add(opponentId);
        }
        return temp;
    }

    private void setupStatusLabel(Label lbl, String text, String bg, String textFill) {
        lbl.setText(text);
        lbl.setStyle("-fx-background-color: " + bg + "; -fx-text-fill: " + textFill + "; "
                + "-fx-background-radius: 10; -fx-padding: 5 10 5 10; -fx-font-weight: bold;");
    }
}
