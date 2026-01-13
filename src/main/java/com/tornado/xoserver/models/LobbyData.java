/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

import java.util.List;

/**
 *
 * @author Hossam
 */
public class LobbyData {
    
    public enum LobbyAction{
        LISTEN, STOP_LISTEN, ADD_ONE, REMOVE_ONE, ERROR
    }
    
    private int senderId;
    private LobbyAction lobbyAction;
    private Player updatedPlayer;
    private List<Player> allPlayers;

    public LobbyData() {
    }

    public LobbyData(int senderId, LobbyAction action, Player updatedPlayer, List<Player> allPlayers) {
        this.senderId = senderId;
        this.lobbyAction = action;
        this.updatedPlayer = updatedPlayer;
        this.allPlayers = allPlayers;
    }
    
    public LobbyData onlinePlayersRequest(int playerId, LobbyAction action, Player player, List<Player> allPlayers) {
        return new LobbyData(playerId,action,player,allPlayers);
    }

    public int getSenderId() {
        return senderId;
    }

    public LobbyAction getLobbyAction() {
        return lobbyAction;
    }

    public Player getUpdatedPlayer() {
        return updatedPlayer;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }
    
}
