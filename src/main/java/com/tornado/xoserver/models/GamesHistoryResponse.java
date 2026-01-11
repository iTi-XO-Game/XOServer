package com.tornado.xoserver.models;

import java.util.List;

public class GamesHistoryResponse
{
    private List<GameHistory> gameModels;

    public GamesHistoryResponse(){}

    public GamesHistoryResponse(List<GameHistory> data)
    {
        gameModels = data;
    }

    public List<GameHistory> getGameModels() {
        return gameModels;
    }

    public void setGameModels(List<GameHistory> data)
    {
        gameModels = data;
    }
}
