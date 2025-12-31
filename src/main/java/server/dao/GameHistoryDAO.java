package server.dao;


import server.model.GameHistory;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import server.database.DBConnection;

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}
