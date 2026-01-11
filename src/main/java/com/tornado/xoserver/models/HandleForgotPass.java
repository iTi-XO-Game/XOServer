package com.tornado.xoserver.models;

import com.tornado.xoserver.database.PlayerDAO;
import com.tornado.xoserver.server.JsonUtils;

public class HandleForgotPass
{
    public static String handle(String jsonRequest)
    {
        System.out.println("This request from clint: " + jsonRequest);

        AuthRequest request = JsonUtils.fromJson(jsonRequest,AuthRequest.class);

        String username = request.getUsername() ;
        String pass = request.getPassword();

        System.out.println("Before DAO");
        System.out.println("User name: " + username);
        Boolean resultOfUpdate = PlayerDAO.updataPlayerPass(username,pass);
        System.out.println("After DAO");

        String response = JsonUtils.toJson(resultOfUpdate);
        System.out.println("This will send to user: " + response);
        return response;

    }
}
