/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author Dell
 */
public class GameHistory {

    private int id;
    private int playerXId;
    private int playerOId;
    private Integer winnerId;
    private boolean draw;
    private long gameDate;

    public GameHistory() {
    }

    public GameHistory(int playerXId, int playerOId,
            Integer winnerId, boolean draw,
            LocalDateTime gameDate
    ) {
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.winnerId = winnerId;
        this.draw = draw;

        this.gameDate = gameDate.atZone(ZoneOffset.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerXId() {
        return playerXId;
    }

    public void setPlayerXId(int playerXId) {
        this.playerXId = playerXId;
    }

    public int getPlayerOId() {
        return playerOId;
    }

    public void setPlayerOId(int playerOId) {
        this.playerOId = playerOId;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    public long getGameDate() {
        return gameDate;
    }

    public void setGameDate(long gameDate) {
        this.gameDate = gameDate;
    }

}
