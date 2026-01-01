package com.tornado.xoserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;


public class Handler
{
    DataInputStream dis ;
    DataOutputStream dos;
    static ConcurrentHashMap<Integer, Handler> listOfHandlers = new ConcurrentHashMap<>(); //Save Threads

    int clintId;
    Message clintMessage;
    Gson gson ;


    public Handler(int clintId,Socket s)
    {
        try {
            this.clintId = clintId;
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());
            gson = new Gson();

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
                try
                {
                    clintMessage = gson.fromJson(dis.readUTF(), Message.class);
                    brodCastMessage(clintMessage);
                }catch (IOException e)
                {
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

    public void brodCastMessage(Message message)
    {
        for (Handler val : listOfHandlers.values())
        {
            String temp = gson.toJson(message);
            System.out.println(temp);

            try {
                val.dos.writeUTF(temp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            System.out.println("=========================");
        }
    }
}
