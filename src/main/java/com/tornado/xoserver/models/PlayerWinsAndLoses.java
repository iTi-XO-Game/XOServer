package com.tornado.xoserver.models;

public class PlayerWinsAndLoses
{
    private int winsCounter;
    private int losesCounter;

    public PlayerWinsAndLoses() {
    }

    public PlayerWinsAndLoses(int winsCounter, int losesCounter) {
        this.winsCounter = winsCounter;
        this.losesCounter = losesCounter;
    }

    public int getWinsCounter() {
        return winsCounter;
    }

    public void getWinsCounter(int winsCounter) {
        this.winsCounter = winsCounter;
    }

    public int getLosesCounter() {
        return losesCounter;
    }

    public void setLosesCounter(int losesCounter) {
        this.losesCounter = losesCounter;
    }
}
