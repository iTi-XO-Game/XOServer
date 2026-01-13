/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;


/**
 *
 * @author Hossam
 */
public class Move {

    private char Player;
    private int ColIndex;
    private int rowIndex;

    public Move() {
    }

    public Move(char player, int rowIndex, int colIndex) {
        this.Player = player;
        this.rowIndex = rowIndex;
        this.ColIndex = colIndex;
    }

    public char getPlayer() {
        return Player;
    }

    public int getRow() {
        return rowIndex;
    }

    public int getCol() {
        return ColIndex;
    }
}
