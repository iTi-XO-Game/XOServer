package com.mycompany.clientside.client.responses;

import com.mycompany.clientside.models.GameModel ;

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
