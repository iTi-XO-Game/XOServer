/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 *
 * @author Hossam
 */
public class JsonUtils {
    
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static <T> String toJson(T object) {
        try {
            return gson.toJson(object);
        } catch (JsonSyntaxException e) {
            System.err.println("Error serializing JSON: " + e.getMessage());
            throw new RuntimeException("Error serializing JSON");
        }
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        try {
            return gson.fromJson(jsonString, clazz);
        } catch (JsonSyntaxException e) {
            System.err.println("Error deserializing JSON: " + e.getMessage());
            throw new RuntimeException("Error deserializing JSON");
        }
    }
}
