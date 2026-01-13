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
    private boolean playing;
    
    
    public Player(){}

    public Player(int id, String username, int wins, int losses, int draws, boolean playing) {
        this.id = id;
        this.username = username;
        this.wins = wins;
        this.losses = losses;
        this.playing = playing;
        this.draws = draws;
    }

    public int getDraws() {
        return draws;
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

    public int getLosses() {
        return losses;
    }

    public boolean isPlaying() {
        return this.playing;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setusername(String username) {
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

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
