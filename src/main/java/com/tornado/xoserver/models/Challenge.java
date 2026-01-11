/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

/**
 *
 * @author Hossam
 */
public class Challenge {

    public enum ChallengeAction {
        LISTEN, STOP_LISTEN, SEND, CANCEL, DECLINE, ACCEPT, DONE, ERROR
    }

    private String id; 
    private ChallengeAction action;
    private Player sender;
    private Player receiver;
    private String errorMessage;

    public Challenge() {
    }

    public Challenge(String id, ChallengeAction action, Player sender, Player receiver, String errorMessage) {
        this.id = id;
        this.action = action;
        this.sender = sender;
        this.receiver = receiver;
        this.errorMessage = errorMessage;
    }

    public ChallengeAction getAction() {
        return action;
    }

    public Player getSender() {
        return sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getId() {
        return id;
    }

    public void setAction(ChallengeAction action) {
        this.action = action;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setId(String id) {
        this.id = id;
    }
}
