/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Dell
 */
public class DBConnection {
    
         private static final String DB_PATH
            = System.getProperty("user.home") + "/XOdatabase";

    private static final String URL
            = "jdbc:derby:" + DB_PATH + ";create=true";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
    
}
