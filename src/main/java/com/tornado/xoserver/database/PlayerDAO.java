/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.database;

import java.sql.Connection;
import java.sql.*;
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


    public static boolean updataPlayerPass(String username, String newPass)
    {
        try(Connection con = DBConnection.getConnection())
        {
            String sql = "Update player set password = ? where username = ?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1,newPass);
            ps.setString(2,username);

            int rowsUpdated = ps.executeUpdate() ;
            
            if (rowsUpdated == 1)
                return true;
            else if (rowsUpdated == 0)
                return  false;
            else {
                throw new IllegalStateException("More than row updated!!!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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

}



