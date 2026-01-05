package com.tornado.xoserver.models;

public class Move {

    private int row;
    private int col;

    public Move() {
    }

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    // getters & setters
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
