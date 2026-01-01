package com.tornado.xoserver;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Clint
{

    public static void main(String[] args)
    {
        new Clint();
    }


    Socket socket;
    DataInputStream dis ;
    DataOutputStream dos;
    Message message;
    Gson gson ;

    public Clint()
    {
        try {
            socket = new Socket("127.0.0.1",5000);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Clint Started");
            gson = new Gson();

            sayHiToServer();

        } catch (IOException e) {
            System.out.println("Server is off\nBy...");
        }

    }

    public void sayHiToServer()
    {
        try {
            // now we want to test and send json message to the server
            message = new Message("Magdy",22);
            // now convert it to json to send
            String temp = gson.toJson(message);
            dos.writeUTF(temp);
        } catch (IOException e) {
            System.out.println("Can't See The Server");
            throw new RuntimeException(e);
        }
    }

}
