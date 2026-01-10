/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import com.tornado.xoserver.models.*;
import com.tornado.xoserver.database.PlayerDAO;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author lenovo
 */
public class ResponseManager {

    private static final ResponseManager INSTANCE = new ResponseManager();

    public static ResponseManager getInstance() {
        return INSTANCE;
    }

    private ResponseManager() {
    }

    private final Set<Player> onlinePlayers = ConcurrentHashMap.newKeySet();

    public String getResponse(String request) {

        int firstSplit = request.indexOf("|");
        String endPointString = request.substring(0, firstSplit);

        EndPoint endPoint = EndPoint.fromString(endPointString);

        int secondSplit = request.indexOf("|", firstSplit + 1);
        String callbackId = request.substring(firstSplit + 1, secondSplit);

        String requestJson = request.substring(secondSplit + 1);

        return endPointString + "|" + callbackId + "|" + processRequest(requestJson, endPoint);
    }

    // todo add parsing logic
    private String processRequest(String requestJson, EndPoint endPoint) {
        String response = "";
        switch (endPoint) {
            case LOGIN -> {
                response = handleLogin(requestJson);
            }
            case REGISTER -> {
                response = handleRegister(requestJson);
            }
            case LOGOUT -> {
                //response = handleLogout(requestJson);
            }
            case ONLINE_USERS -> {
                //response = handleOnlineUsers(requestJson);
            }
            case JOIN_GAME -> {
                //response = handleJoinGame(requestJson);
            }
            case LEAVE_GAME -> {
                //response = handleLeaveGame(requestJson);
            }
            case UPDATE_USER_PASS -> {
                response = HandleForgotPass.handle(requestJson);
            }
        }
        return response;
    }

    // I know that this function may not be placed on the best place, but for now let's celebrate that it's actually working
    private String handleLogin(String requestJson) {
        AuthRequest loginRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = new PlayerDAO();
        Player p = playerDao.loginPlayer(loginRequest);
        if (p == null) {
            return JsonUtils.toJson(new AuthResponse(StatusCode.ERROR, "No User Found"));
        } else {
            AuthResponse authResponse = new AuthResponse(StatusCode.SUCCESS, p.getId(), p.getUsername());
             
            return JsonUtils.toJson(authResponse);
        }
    }

    private String handleRegister(String requestJson) {
        AuthRequest registerRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = new PlayerDAO();
        if(playerDao.createPlayer(registerRequest.getUsername(), registerRequest.getPassword())){
            return JsonUtils.toJson(new AuthResponse(StatusCode.SUCCESS));
        }
        else{
            return JsonUtils.toJson(new AuthResponse(StatusCode.ERROR, "The User Name Already Exists"));
        }
    }
}
