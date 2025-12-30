package com.tornado.xoserver;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Handler
{
    DataInputStream dis ;
    DataOutputStream dos;
    static ConcurrentHashMap<Integer, Handler> listOfHandlers = new ConcurrentHashMap<>(); //Save Threads

    int clintId;
    String clintMessage;
    
    public Handler(int clintId,Socket s)
    {
        try {
            this.clintId = clintId;
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            listOfHandlers.put(clintId, this);
            listenToClint();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenToClint()
    {
        new Thread(() ->
        {
            while(true)
            {
                try {
                    clintMessage = dis.readUTF();
                    brodCastMessage(clintMessage);

                } catch (IOException e) {
                    System.out.println("Clint out now");

                    try {
                        dis.close();
                        dos.close();
                        break;
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
                ).start();

    }

    public void brodCastMessage(String message)
    {
        for (Handler val : listOfHandlers.values())
        {
            System.out.println("CLint " + val.clintId + ": " + message);
//                        val.dos.writeUTF("CLint " + clintId + ": " + message); // no thread to listen now
        }
    }
}
