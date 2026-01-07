package com.tornado.xoserver.models.responses;

import com.tornado.xoserver.models.GameModel;

import java.util.ArrayList;

public class GamesHistoryResponse
{
    private ArrayList<GameModel> gameModels;

    public GamesHistoryResponse(){}

    public GamesHistoryResponse(ArrayList<GameModel> data)
    {
        gameModels = data;
    }

    public ArrayList<GameModel> getGameModels() {
        return gameModels;
    }

    public void setGameModels(ArrayList<GameModel> data)
    {
        gameModels = data;
    }
}
