/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

/**
 *
 * @author Dell
 */

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ServerLog {

    private static Consumer<String> uiConsumer;

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private ServerLog() {}

    // Dashboard registers here
    public static void setUiConsumer(Consumer<String> consumer) {
        uiConsumer = consumer;
    }

    public static void log(String level, String message) {
        String log =
                "[" + LocalTime.now().format(TIME) + "] "
                + level + ": " + message;



        // UI
        if (uiConsumer != null) {
            uiConsumer.accept(log);
        }
    }

    // helpers
    public static void info(String msg) {
        log("INFO", msg);
    }

    public static void warn(String msg) {
        log("WARN", msg);
    }

    public static void error(String msg) {
        log("ERROR", msg);
    }
}
