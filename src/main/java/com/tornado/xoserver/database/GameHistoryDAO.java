package com.tornado.xoserver.database;


import com.tornado.xoserver.models.GameHistory;
import com.tornado.xoserver.models.GameModel;

import java.sql.*;
import java.time.LocalDateTime;
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
            ps.setTimestamp(5, Timestamp.valueOf(game.getGameDate()));

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
                game.setGameDate(ts.toLocalDateTime());

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
                game.setGameDate(ts.toLocalDateTime());

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

    public static ArrayList<GameModel>  getPlayerGames(int playerId)
    {
        ArrayList<GameModel>gameModels = new ArrayList<>();

        LocalDateTime matchStart = LocalDateTime.of(2023, 10, 24, 14, 30, 0);
        LocalDateTime matchEnd = LocalDateTime.of(2023, 10, 24, 14, 34, 21);
        gameModels.add(new GameModel(1, 100, 200, 100, matchStart, matchEnd));
        gameModels.add(new GameModel(2, 100, 200, -1, matchStart, matchEnd));
        gameModels.add(new GameModel(3, 100, 200, 200, matchStart, matchEnd));
        gameModels.add(new GameModel(3, 100, 200, 200, matchStart, matchEnd));
        gameModels.add(new GameModel(3, 100, 200, 200, matchStart, matchEnd));
        gameModels.add(new GameModel(3, 100, 200, 200, matchStart, matchEnd));

        return gameModels;
    }
}
