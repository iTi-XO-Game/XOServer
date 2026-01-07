package com.mycompany.clientside.client.requests;

public class GamesHistoryRequest
{
    private  int clientID;

    public GamesHistoryRequest(){}

    public GamesHistoryRequest(int id)
    {
        clientID = id;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }
}
