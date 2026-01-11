package com.tornado.xoserver.models;

public class LogIn {

    private String username;
    private String pass;

    public LogIn() {
    }

    public LogIn(String username, String pass) {
        this.username = username;
        this.pass = pass;
    }

    // getters & setters
    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
