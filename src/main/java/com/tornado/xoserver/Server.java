package com.tornado.xoserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    int clintId = 0;

    public static void main(String[] args)
    {
        new Server();
    }

    ServerSocket serverSocket ;
    Socket clintSocket;

    public Server()
    {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server Started");

        } catch (IOException e) {
            System.out.println("Server Can't Start");
            throw new RuntimeException(e);
        }

        while(true)
        {
            try {
                clintId++;
                clintSocket = serverSocket.accept();
                new Handler(clintId,clintSocket);

            } catch (IOException e) {
                System.out.println("Server Can't Accept The Clint");
                throw new RuntimeException(e);
            }
            /*
            I have to make handler for handling the clintSocket
            1) create DIS and DOS
            2) Storge all the handlers in list

            * */
        }
    }
}
