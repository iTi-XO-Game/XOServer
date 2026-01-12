/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author Depogramming
 */
public class Stats {
    public static IntegerProperty total = new SimpleIntegerProperty();
    public IntegerProperty online = new SimpleIntegerProperty();
    public IntegerProperty offline = new SimpleIntegerProperty();
    public IntegerProperty sessions = new SimpleIntegerProperty();
}
