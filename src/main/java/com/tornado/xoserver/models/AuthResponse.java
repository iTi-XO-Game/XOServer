/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

/**
 *
 * @author Depogramming
 */
public class AuthResponse {

    private StatusCode statusCode;
    private String errorMessage;
    private Integer id;
    private String username;
    
    private Player player;

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AuthResponse(StatusCode statusCode, String errorMessage) {
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    
    public AuthResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public AuthResponse() {
    }

    public AuthResponse(StatusCode statusCode, Integer id, String userName) {
        this.statusCode = statusCode;
        this.id = id;
        this.username = userName;
    }
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
