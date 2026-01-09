package com.tornado.xoserver.server.handling;

import com.tornado.xoserver.database.GameHistoryDAO;
import com.tornado.xoserver.models.GameHistory;
import com.tornado.xoserver.models.GamesHistoryRequest;
import com.tornado.xoserver.models.GamesHistoryResponse;
import com.tornado.xoserver.server.JsonUtils;

import java.util.ArrayList;

public class GamesHistoryHandling
{
    public static String getGamesHistory(String requestJson)
    {
        GamesHistoryRequest request = JsonUtils.fromJson(requestJson,GamesHistoryRequest.class);
        ArrayList<GameHistory> data = GameHistoryDAO.getPlayerGames(request.getClientID());
        GamesHistoryResponse response = new GamesHistoryResponse(data);

        String temp = JsonUtils.toJson(response);

        return  temp;
    }
}
