/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

/**
 *
 * @author Hossam
 */
public class ActiveGame {

    public enum GameAction {
        LISTEN, STOP_LISTEN, START, RESTART, GIVE_UP, MOVE, ERROR
    }

    private String id;
    private GameAction action;
    private boolean isGameOn;
    private char currentPlayer;
    private Move latestMove;
    private int playerXid;
    private int playerOid;
    private int winnerId;
    private long gameDate;
    private Player sender;
    private Player receiver;
    private String errorMessage;

    public void reset() {
        isGameOn = false;
        currentPlayer = 'X';
        latestMove = null;
        winnerId = -1;
        gameDate = System.currentTimeMillis();
        errorMessage = "";
    }

    public ActiveGame() {
    }

    public ActiveGame(String id,
                      GameAction action,
                      boolean isGameOn,
                      char currentPlayer,
                      Move latestMove,
                      int playerXid,
                      int playerOid,
                      int winnerId,
                      long gameDate,
                      Player sender,
                      Player receiver,
                      String errorMessage) {
        this.id = id;
        this.action = action;
        this.isGameOn = isGameOn;
        this.currentPlayer = currentPlayer;
        this.latestMove = latestMove;
        this.playerXid = playerXid;
        this.playerOid = playerOid;
        this.winnerId = winnerId;
        this.gameDate = gameDate;
        this.sender = sender;
        this.receiver = receiver;
        this.errorMessage = errorMessage;
    }

    public ActiveGame(String id,
                      GameAction action,
                      Player sender,
                      Player receiver,
                      String errorMessage) {
        this.id = id;
        this.action = action;
        this.sender = sender;
        this.receiver = receiver;
        this.errorMessage = errorMessage;
    }

    public static ActiveGame getActiveGame(ActiveGame activeGame) {
        return new ActiveGame(
                activeGame.id,
                activeGame.action,
                activeGame.isGameOn,
                activeGame.currentPlayer,
                activeGame.latestMove,
                activeGame.playerXid,
                activeGame.playerOid,
                activeGame.winnerId,
                activeGame.gameDate,
                activeGame.sender,
                activeGame.receiver,
                activeGame.errorMessage
        );
    }

    public static ActiveGame DEFAULT = new ActiveGame(
            "",
            GameAction.START,
            false,
            'X',
            new Move(),
            -1,
            -1,
            -1,
            System.currentTimeMillis(),
            new Player(),
            new Player(),
            ""
    );

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameAction getAction() {
        return action;
    }

    public void setAction(GameAction action) {
        this.action = action;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Move getLatestMove() {
        return latestMove;
    }

    public void setLatestMove(Move latestMove) {
        this.latestMove = latestMove;
    }

    public int getPlayerXid() {
        return playerXid;
    }

    public void setPlayerXid(int playerXid) {
        this.playerXid = playerXid;
    }

    public int getPlayerOid() {
        return playerOid;
    }

    public void setPlayerOid(int playerOid) {
        this.playerOid = playerOid;
    }

    public long getGameDate() {
        return gameDate;
    }

    public void setGameDate(long gameDate) {
        this.gameDate = gameDate;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean getIsGameOn() {
        return isGameOn;
    }

    public void setIsGameOn(boolean isGameOn) {
        this.isGameOn = isGameOn;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

}