/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author lenovo
 */
public class XOClient {

    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private static final GameManager gameManager = GameManager.getInstance();

    public XOClient() {}

    public void connect(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        ) {
            isConnected.set(true);

            String request;
            while ((request = reader.readLine()) != null) {
                
                if (request.isBlank()) {
                    continue;
                }
                String response = getResponse(request.trim());

                writer.println(response);
            }
        } catch (IOException ex) {
            
        } finally {
            disconnect(socket);
        }
    }

    private String getResponse(String request) {
        return gameManager.getResponse(request);
    }
    
    private void disconnect(Socket socket) {
        isConnected.set(false);
        
        try {
            if (!socket.isClosed()){ 
                socket.close();
            }
        } catch (IOException ex) {
        }
    }
    
    public boolean isClientConnected() {
        return isConnected.get();
    }
}
