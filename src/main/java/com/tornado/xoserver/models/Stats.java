/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Depogramming
 */
public class Stats {
    public static List<String> allPlayers = new ArrayList<String>();
    public static IntegerProperty total = new SimpleIntegerProperty();

    public static List<String> allOnlinePlayers = new ArrayList<String>();
    public static IntegerProperty online = new SimpleIntegerProperty();

    public static List<String> allOfflinePlayers = new ArrayList<String>();
    public IntegerProperty offline = new SimpleIntegerProperty();
    public IntegerProperty sessions = new SimpleIntegerProperty();
}
