package com.tornado.xoserver.server;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author lenovo
 */
public enum EndPoint {

    LOGIN("LOGIN"),
    REGISTER("REGISTER"),
    LOGOUT("LOGOUT"),

    SEND_CHALLENGE("SEND_CHALLENGE"),
    RECEIVE_CHALLENGE("RECEIVE_CHALLENGE"),
    ONLINE_USERS("ONLINE_USERS"),
    JOIN_GAME("JOIN_GAME"),
    PLAYER_GAMES_HISTORY("PLAYER_GAMES_HISTORY"),
    PLAYER_ID("PLAYER_ID"),
    LEAVE_GAME("LEAVE_GAME");

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
