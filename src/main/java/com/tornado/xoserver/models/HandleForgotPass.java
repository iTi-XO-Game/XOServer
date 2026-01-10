package com.tornado.xoserver.models;

import com.tornado.xoserver.server.JsonUtils;

public class HandleForgotPass
{
    public static String handle(String jsonRequest)
    {
        AuthRequest request = JsonUtils.fromJson(jsonRequest,AuthRequest.class);

        return "";
    }
}
