/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Hossam
 */
public class JsonUtils {
    
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static <T> String toJson(T object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        return gson.fromJson(jsonString, clazz);
    }
}
