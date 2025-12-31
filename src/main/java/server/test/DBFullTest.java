package server.test;

import server.database.DBConnection;
import server.dao.PlayerDAO;
import server.model.GameHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

public class DBFullTest {

    public static void main(String[] args) {

        try {
            
            Connection con = DBConnection.getConnection();
            System.out.println("Database Connected Successfully");
            con.close();

            
            PlayerDAO playerDAO = new PlayerDAO();
            playerDAO.createPlayer("TestPlayer1", "123");
            playerDAO.createPlayer("TestPlayer2", "456");
            System.out.println("Players Inserted");

           
            GameHistory game = new GameHistory(
                    1,      
                    2,      
                    1,      
                    false,  
                    null
            );

            String sql =
                "INSERT INTO GamesHistory " +
                "(playerXId, playerOId, winnerId, isDraw) " +
                "VALUES (?, ?, ?, ?)";

            try (Connection c = DBConnection.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, game.getPlayerXId());
                ps.setInt(2, game.getPlayerOId());

                if (game.getWinnerId() == null)
                    ps.setNull(3, Types.INTEGER);
                else
                    ps.setInt(3, game.getWinnerId());

                ps.setInt(4, game.isDraw() ? 1 : 0);

                ps.executeUpdate();
                System.out.println("Game History Inserted");
            }

            playerDAO.updatePlayerStats(1, 2, false);
            System.out.println("Player Stats Updated");

            System.out.println("ALL TESTS PASSED SUCCESSFULLY");

        } catch (Exception e) {
            System.out.println("ERROR OCCURRED");
            e.printStackTrace();
        }
    }
}
