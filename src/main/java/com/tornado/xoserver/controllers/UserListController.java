package com.tornado.xoserver.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class UserListController {

    @FXML
    private Label titleLabel;
    @FXML
    private ListView<String> usersList;

    public void setData(String title, List<String> users) {
        titleLabel.setText(title);
        usersList.getItems().setAll(users);
    }
}
