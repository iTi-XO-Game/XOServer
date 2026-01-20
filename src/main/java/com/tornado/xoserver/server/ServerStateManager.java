package com.tornado.xoserver.server;

import com.tornado.xoserver.models.ActiveGame;
import com.tornado.xoserver.models.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Hossam
 */
public class ServerStateManager {

    private static final ServerStateManager INSTANCE = new ServerStateManager();

    private final Map<Integer, XOClient> activeConnections = new ConcurrentHashMap<>();
    private final Map<Integer, Player> onlinePlayers = new ConcurrentHashMap<>();
    private final Set<Integer> challengeListeners = ConcurrentHashMap.newKeySet();
    private final Set<Integer> lobbyListeners = ConcurrentHashMap.newKeySet();

    private final Map<String, ActiveGame> activeGames = new ConcurrentHashMap<>();
    private final Map<Integer, String> playersInGames = new ConcurrentHashMap<>();

    public static ServerStateManager getInstance() {
        return INSTANCE;
    }

    private ServerStateManager() {
    }

    public void addActiveConnection(int id, XOClient client) {
        activeConnections.put(id, client);
    }

    public void removeActiveConnection(int id) {
        activeConnections.remove(id);
    }

    public void addOnlinePlayer(Player player) {
        onlinePlayers.put(player.getId(), player);
    }

    public void removeOnlinePlayer(int id) {
        onlinePlayers.remove(id);
    }


    public void addChallengeListener(int id) {
        challengeListeners.add(id);
    }

    public void removeChallengeListener(int id) {
        challengeListeners.remove(id);
    }


    public void addLobbyListener(int id) {
        lobbyListeners.add(id);
    }

    public void removeLobbyListener(int id) {
        lobbyListeners.remove(id);
    }

    public void addActiveGame(String id, ActiveGame game) {
        activeGames.put(id, game);
    }

    public ActiveGame getActiveGame(String id) {
        return activeGames.get(id);
    }

    public void removeActiveGame(String id) {
        activeGames.remove(id);
    }

    public void addPlayerInGame(int id, String gameId) {
        playersInGames.put(id, gameId);
    }

    public String getPlayerInGame(int pid) {
        return playersInGames.get(pid);
    }

    public void removePlayerInGame(int id) {
        playersInGames.remove(id);
    }

}
