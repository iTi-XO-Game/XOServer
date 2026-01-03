package com.tornado.xoserver;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.util.List;

public class UserListController {

    @FXML
    private Label screenTitle;

    @FXML
    private ListView<String> usersListView;

    public void setData(String title, List<String> users) {
        screenTitle.setText(title);
        usersListView.getItems().setAll(users);
    }
}
