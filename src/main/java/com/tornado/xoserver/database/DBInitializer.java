/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Depogramming
 */
public class DBInitializer {


    public static boolean tableExists(Connection con, String tableName) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next();
        }
    }

    public static void init() {
        try (Connection con = DBConnection.getConnection(); Statement stmt = con.createStatement()) {

            if (!tableExists(con, "PLAYER")) {

                stmt.executeUpdate(
                        "CREATE TABLE Player ("
                        + "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                        + "username VARCHAR(50) UNIQUE NOT NULL, "
                        + "password VARCHAR(50) NOT NULL, "
                        + "wins INT DEFAULT 0, "
                        + "draws INT DEFAULT 0, "
                        + "losses INT DEFAULT 0"
                        + ")"
                );

                stmt.executeUpdate(
                        "CREATE TABLE GamesHistory ("
                        + "id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                        + "playerXId INT NOT NULL, "
                        + "playerOId INT NOT NULL, "
                        + "gameDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                        + "winnerId INT, "
                        + "isDraw SMALLINT NOT NULL CHECK (isDraw IN (0,1)), "
                        + "CONSTRAINT fk_playerX FOREIGN KEY (playerXId) REFERENCES Player(id), "
                        + "CONSTRAINT fk_playerO FOREIGN KEY (playerOId) REFERENCES Player(id), "
                        + "CONSTRAINT fk_winner FOREIGN KEY (winnerId) REFERENCES Player(id)"
                        + ")"
                );

            } 

        } catch (SQLException e) {
        }
    }
    
}
