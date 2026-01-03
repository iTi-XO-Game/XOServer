/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

/**
 *
 * @author lenovo
 */
public class GameManager {
    
    private static final GameManager INSTANCE = new GameManager();

    public static GameManager getInstance() {
        return INSTANCE;
    }

    private GameManager() {
    }
    
    public String getResponse(String request) {
        int splitIndex = request.indexOf("|");
        String callbackId = request.substring(0, splitIndex);
        
        String requestJson = request.substring(splitIndex + 1);
        
        return callbackId + "|" + processRequest(requestJson);
    }
    
    // todo add parsing logic
    private String processRequest(String requestJson) {
        return "TODO getResponse for " + requestJson;
    }
}
