package com.tornado.xoserver.models;

import java.util.List;

public class GamesHistoryResponse
{
    private List<GameHistory> gameModels;
    private PlayerWinsAndLoses playerWinsAndLoses;

    public GamesHistoryResponse(){}

    public GamesHistoryResponse(List<GameHistory> data, PlayerWinsAndLoses playerWinsAndLoses)
    {
        gameModels = data;
        this.playerWinsAndLoses = playerWinsAndLoses;
    }

    public List<GameHistory> getGameModels() {
        return gameModels;
    }

    public void setGameModels(List<GameHistory> data)
    {
        gameModels = data;
    }

    public PlayerWinsAndLoses getPlayerWinsAndLoses() {
        return playerWinsAndLoses;
    }

    public void setPlayerWinsAndLoses(PlayerWinsAndLoses playerWinsAndLoses) {
        this.playerWinsAndLoses = playerWinsAndLoses;
    }
}
