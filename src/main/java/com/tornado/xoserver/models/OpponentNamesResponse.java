package com.tornado.xoserver.models;

import java.util.HashMap;
import java.util.Map;

public class OpponentNamesResponse {

    private Map<Integer, String> usersMap;

    public OpponentNamesResponse() {
        this.usersMap = new HashMap<>();
    }

    public OpponentNamesResponse(Map<Integer, String> usersMap) {
        this.usersMap = usersMap;
    }

    public Map<Integer, String> getOpponentsMap() {
        return usersMap;
    }

    public void setOpponentsMap(Map<Integer, String> usersMap) {
        this.usersMap = usersMap;
    }
}
