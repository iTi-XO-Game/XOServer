/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author Hossam
 */
public class ServerManager {

    private static final ServerManager INSTANCE = new ServerManager();

    public static ServerManager getInstance() {
        return INSTANCE;
    }

    private ServerManager() {
    }

    private final int PORT = 8181;
    private ServerSocket serverSocket;

    private ExecutorService executor;
    private Thread serverThread;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void startServer(Runnable callback) {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        executor = Executors.newVirtualThreadPerTaskExecutor();

        serverThread = Thread.startVirtualThread(this::runServer);
        callback.run();
    }

    private void runServer() {
        try {
            serverSocket = new ServerSocket(PORT);

            while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
                Socket clientSocket = serverSocket.accept();

                executor.submit(() -> {
                    new XOClient().connect(clientSocket);
                });
            }

        } catch (IOException ex) {
            stopServer(() -> {});
        }
    }

    public void stopServer(Runnable callback) {
        if (!isRunning.get()) return;
        new Thread(() -> {
            try {
                if (serverThread != null) {
                    serverThread.interrupt();
                    serverThread = null;
                }
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                    serverSocket = null;
                }
                if (executor != null) {
                    executor.shutdown();
                    if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                    executor = null;
                }
                isRunning.set(false);
                callback.run();
            } catch (IOException | InterruptedException ex) {
                showErrorAlert(
                        "Server Error",
                        "Unable to stop the server!",
                        "Please, try again."
                );
            }
        }).start();
    }
    
    private void showErrorAlert(String title, String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }
    
    public boolean isServerRunning() {
        return isRunning.get();
    }
}
