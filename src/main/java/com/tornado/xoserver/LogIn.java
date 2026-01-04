package com.tornado.xoserver;

public class LogIn {

    private String userName;
    private String pass;

    public LogIn() {
    }

    public LogIn(String userName, String pass) {
        this.userName = userName;
        this.pass = pass;
    }

    // getters & setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
