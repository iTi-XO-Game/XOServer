/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

/**
 *
 * @author Dell
 */
public class Player {

    private int id;
    private String username;
    private int wins;
    private int draws;
    private int losses;
    private boolean isInGame;

    public Player() {
    }

    public Player(int id, String username,
            int wins, int draws,
            int losses,
            boolean isInGame
    ) {
        this.id = id;
        this.username = username;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.isInGame = isInGame;
    }

    public boolean isIsInGame() {
        return isInGame;
    }

    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

}
