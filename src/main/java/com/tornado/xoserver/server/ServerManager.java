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
 * @author lenovo
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

    public void startServer() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        executor = Executors.newVirtualThreadPerTaskExecutor();

        serverThread = Thread.startVirtualThread(this::runServer);
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
            if (isRunning.get()) {
                showErrorAlert(
                        "Server Error",
                        "Unable to start the server!",
                        "Please, check the port number and try again."
                );
                stopServer();
            }
        }
    }

    public void stopServer() {
        if (!isRunning.compareAndSet(true, false)) {
            return;
        }

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
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
                executor = null;
            }
        } catch (IOException | InterruptedException ex) {
            showErrorAlert(
                    "Server Error",
                    "Unable to stop the server!",
                    "Please, try again."
            );
        }
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
