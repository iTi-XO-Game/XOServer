package com.tornado.xoserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Clint {

    public static void main(String[] args)
    {
        new Clint();
    }


    Socket socket;
    DataInputStream dis ;
    DataOutputStream dos;

    public Clint()
    {
        try {
            socket = new Socket("127.0.0.1",5000);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Clint Started");

            sayHiToServer();


        } catch (IOException e) {
            System.out.println("Server is off\nBy...");
        }

    }

    public void sayHiToServer()
    {
        try {
            dos.writeUTF("Hi");
        } catch (IOException e) {
            System.out.println("Can't See The Server");
            throw new RuntimeException(e);
        }
    }

}
