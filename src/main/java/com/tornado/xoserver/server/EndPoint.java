package com.tornado.xoserver.server;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Hossam
 */
public enum EndPoint {

    LOGIN("LOGIN"),
    REGISTER("REGISTER"),
    LOGOUT("LOGOUT"),
    
    LOBBY("LOBBY"),
    CHALLENGE("CHALLENGE"),
    GAME("GAME");
  
    UPDATE_USER_PASS("UPDATE_USER_PASS"),
    PLAYER_GAMES_HISTORY("PLAYER_GAMES_HISTORY"),
    
    private final String code;

    private EndPoint(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static EndPoint fromString(String actionName) {
        for (EndPoint endPoint : values()) {
            if (endPoint.code.equals(actionName)) return endPoint;
        }
        throw new IllegalArgumentException("Unknown endpoint: " + actionName);
    }
}
