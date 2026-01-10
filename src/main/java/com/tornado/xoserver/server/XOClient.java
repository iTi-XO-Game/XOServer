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
 * @author Hossam
 */
public class XOClient {

    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private static final ResponseManager responseManager = ResponseManager.getInstance();
    private PrintWriter writer;

    public XOClient() {
    }

    public void connect(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
            writer = new PrintWriter(socket.getOutputStream(), true);
            isConnected.set(true);

            String request;
            while ((request = reader.readLine()) != null) {

                if (request.isBlank()) {
                    continue;
                }
                String response = responseManager.getResponse(request.trim(), this);

                sendToClient(response);
            }
        } catch (IOException ex) {

        } finally {
            disconnect(socket);
        }
    }

    private boolean sendToClient(String response) {
        if (writer != null) {
            writer.println(response);
            return true;
        }
        return false;
    }    

    /**
     * @return false means client is disconnected
     */
    public boolean sendToListener(EndPoint endPoint, String response) {
        return sendToClient(endPoint.getCode() + "|" + -1 + "|" + response);
    }

    private void disconnect(Socket socket) {
        isConnected.set(false);
        if (writer != null) {
            writer.close();
        }
        writer = null;
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
        }
    }

    public boolean isClientConnected() {
        return isConnected.get();
    }
}
