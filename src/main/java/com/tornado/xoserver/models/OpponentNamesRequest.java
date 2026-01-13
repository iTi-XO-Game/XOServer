package com.tornado.xoserver.models;

import java.util.ArrayList;
import java.util.List;

public class OpponentNamesRequest {

    private List<Integer> usersIds;

    public OpponentNamesRequest() {
        this.usersIds = new ArrayList<>();
    }

    public OpponentNamesRequest(List<Integer> usersIds) {
        this.usersIds = usersIds;
    }

    public List<Integer> getOpponentsIds() {
        return usersIds;
    }

    public void setOpponentsIds(List<Integer> usersIds) {
        this.usersIds = usersIds;
    }
}
