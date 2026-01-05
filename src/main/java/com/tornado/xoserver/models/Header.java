package com.tornado.xoserver.models;
/*
should has
1) enum has messageType
2) enum has actionType
*/



public class Header
{
    enum MessageType {
        REQUEST,
        RESPONSE,
        ERROR,
        EVENT
    }

    enum ActionType {
        MOVE,
        LOGIN
    }


    private MessageType messageType;
    private ActionType actionType;


    public Header() {

    }

    public Header(MessageType messageType, ActionType actionType) {
        this.messageType = messageType;
        this.actionType = actionType;
    }

    // ===== Getters & Setters =====
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

}
