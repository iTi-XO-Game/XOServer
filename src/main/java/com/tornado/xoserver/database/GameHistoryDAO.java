package com.tornado.xoserver.database;


import com.tornado.xoserver.models.GameHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryDAO {

       public boolean saveGame(GameHistory game) {
        String sql = "INSERT INTO GamesHistory(playerXId, playerOId, winnerId, isDraw, gameDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, game.getPlayerXId());
            ps.setInt(2, game.getPlayerOId());

            if (game.getWinnerId() != null)
                ps.setInt(3, game.getWinnerId());
            else
                ps.setNull(3, Types.INTEGER);

            ps.setInt(4, game.isDraw() ? 1 : 0);
            ps.setTimestamp(5, new Timestamp(game.getGameDate()));

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }


    public GameHistory getGameById(int id) {
        String sql = "SELECT * FROM GamesHistory WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                GameHistory game = new GameHistory();
                game.setId(rs.getInt("id"));
                game.setPlayerXId(rs.getInt("playerXId"));
                game.setPlayerOId(rs.getInt("playerOId"));

                int winner = rs.getInt("winnerId");
                if (rs.wasNull()) game.setWinnerId(null);
                else game.setWinnerId(winner);

                game.setDraw(rs.getInt("isDraw") == 1);
                Timestamp ts = rs.getTimestamp("gameDate");
                game.setGameDate(ts.getTime());

                return game;
            }

        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return null;
    }

    public List<GameHistory> getAllGames() {
        List<GameHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM GamesHistory";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                GameHistory game = new GameHistory();
                game.setId(rs.getInt("id"));
                game.setPlayerXId(rs.getInt("playerXId"));
                game.setPlayerOId(rs.getInt("playerOId"));

                int winner = rs.getInt("winnerId");
                if (rs.wasNull()) game.setWinnerId(null);
                else game.setWinnerId(winner);

                game.setDraw(rs.getInt("isDraw") == 1);

                Timestamp ts = rs.getTimestamp("gameDate");
                game.setGameDate(ts.getTime());

                list.add(game);
            }

        } catch (SQLException e) {
            //e.printStackTrace();
        }
        return list;
    }

    public boolean deleteGame(int gameId) {
        String sql = "DELETE FROM GamesHistory WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, gameId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public ArrayList<GameHistory> getPlayerGames(int playerId) {

        ArrayList<GameHistory> gameHistories = new ArrayList<>();

        String sql = "SELECT * FROM GAMESHISTORY WHERE playerXId = ? OR playerOId = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            ps.setInt(2, playerId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                GameHistory game = new GameHistory();

                game.setId(rs.getInt("id"));
                game.setPlayerXId(rs.getInt("playerXId"));
                game.setPlayerOId(rs.getInt("playerOId"));

                Integer winner = rs.getObject("winnerId", Integer.class);
                game.setWinnerId(winner);

                game.setDraw(rs.getBoolean("isDraw"));

                Timestamp ts = rs.getTimestamp("gameDate");
                if (ts != null)
                    game.setGameDate(ts.getTime());

                gameHistories.add(game);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gameHistories;
    }

}
