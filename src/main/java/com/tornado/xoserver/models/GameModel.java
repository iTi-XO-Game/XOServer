/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

import java.time.Duration;
import java.time.LocalDateTime;

public class GameModel {

    private int gameId;
    private int playerOneId;
    private int playerTwoId;
    private int winnerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String duration; // This will be calculated automatically

    public GameModel() {
        this.gameId = 0;
        this.playerOneId = 0;
        this.playerTwoId = 0;
        this.winnerId = 0;
        this.startTime = null;
        this.endTime = null;
        this.duration = "0s";
    }

    public GameModel(int gameId, int playerOneId, int playerTwoId, int winnerId, LocalDateTime startTime, LocalDateTime endTime) {
        this.gameId = gameId;
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
        this.winnerId = winnerId;
        this.startTime = startTime;
        this.endTime = endTime;

        if (startTime != null && endTime != null) {
            Duration diff = Duration.between(startTime, endTime);
            long totalSeconds = diff.getSeconds();
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            this.duration = String.format("%dm %ds", minutes, seconds);
        } else {
            this.duration = "N/A";
        }
    }

    public int getWinnerId() {
        
        return this.winnerId;
    }

    public int getPlayerOneId() {
        return this.playerOneId;
    }

    public int getPlayerTwoId() {
        return this.playerTwoId;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public String getDuration() {
        return this.duration;
    }
}
