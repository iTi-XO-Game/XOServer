/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.database;

import java.sql.Connection;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tornado.xoserver.models.Player;
import com.tornado.xoserver.models.AuthRequest;


/**
 *
 * @author Dell
 */
public class PlayerDAO {

     public boolean createPlayer(String username, String password) {
        String sql = "INSERT INTO Player(username, password) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
              PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            return true;

        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public Player getPlayerById(int id) {
        String sql = "SELECT * FROM Player WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("id"));
                p.setusername(rs.getString("username"));
                p.setWins(rs.getInt("wins"));
                p.setDraws(rs.getInt("draws"));
                p.setLosses(rs.getInt("losses"));
                return p;
            }

        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }


    public static Boolean updataPlayerPass(String username, String newPass)
    {
        try(Connection con = DBConnection.getConnection())
        {
            String sql = "Update player set password = ? where username = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1,newPass);
            ps.setString(2,username);

            int rowsUpdated = ps.executeUpdate() ;

            return rowsUpdated == 1;
            
        } catch (SQLException e) {
            return false;
        }
    }


    public boolean  updatePlayerStats(int winnerId, int loserId, boolean isDraw) {
        try (Connection con = DBConnection.getConnection()) {

            if (isDraw) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE Player SET draws = draws + 1 WHERE id IN (?, ?)");
                ps.setInt(1, winnerId);
                ps.setInt(2, loserId);
                ps.executeUpdate();
            } else {
                PreparedStatement psWin =
                        con.prepareStatement("UPDATE Player SET wins = wins + 1 WHERE id=?");
                psWin.setInt(1, winnerId);
                psWin.executeUpdate();

                PreparedStatement psLose =
                        con.prepareStatement("UPDATE Player SET losses = losses + 1 WHERE id=?");
                psLose.setInt(1, loserId);
                psLose.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }


    public boolean deletePlayer(int playerId) {
        String sql = "DELETE FROM Player WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }
     public Player loginPlayer(AuthRequest loginRequest) {
        String sql = "SELECT * FROM Player WHERE password=? AND username =?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loginRequest.getPassword());
            ps.setString(2, loginRequest.getUsername());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Player p = new Player();
                p.setId(rs.getInt("id"));
                p.setusername(rs.getString("username"));
                p.setWins(rs.getInt("wins"));
                p.setDraws(rs.getInt("draws"));
                p.setLosses(rs.getInt("losses"));
                return p;
            }

        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, String> getUsernames(List<Integer> usersIds) {

        Map<Integer, String> temp = new HashMap<>();

        if (usersIds == null || usersIds.isEmpty()) {
            return temp;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < usersIds.size(); i++) {
            placeholders.append("?");
            if (i < usersIds.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT id, username FROM PLAYER WHERE id IN (" + placeholders + ")";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            for (int i = 0; i < usersIds.size(); i++) {
                ps.setInt(i + 1, usersIds.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");

                temp.put(id, username);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp;
    }

}



