package com.tornado.xoserver.server;

import com.tornado.xoserver.models.ActiveGame;
import com.tornado.xoserver.models.LobbyData;
import com.tornado.xoserver.models.Player;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 *
 * @author Hossam
 */
public class ServerStateManager {

    private static final ServerStateManager INSTANCE = new ServerStateManager();

    private final Map<Integer, XOClient> activeConnections = new ConcurrentHashMap<>();
    private final Map<Integer, Player> onlinePlayers = new ConcurrentHashMap<>();
    private final Set<Integer> lobbyListeners = ConcurrentHashMap.newKeySet();

    private final Map<String, ActiveGame> activeGames = new ConcurrentHashMap<>();
    private final Map<Integer, String> playersInGames = new ConcurrentHashMap<>();

    private final AtomicInteger observerIdGenerator = new AtomicInteger(0);
    private final Map<Integer, BiConsumer<Player, PlayerAction>> onlinePlayersObservers = new ConcurrentHashMap<>();

    public static ServerStateManager getInstance() {
        return INSTANCE;
    }

    private ServerStateManager() {
    }

    public void addActiveConnection(int id, XOClient client) {
        activeConnections.put(id, client);
    }

    public XOClient getActiveConnection(int id) {
        return activeConnections.get(id);
    }

    public void broadcastToAllConnections(EndPoint endPoint, String responseJson) {
        activeConnections.forEach(
                (id, client) -> client.sendToListener(endPoint, responseJson)
        );
    }

    public void removeActiveConnection(int id) {
        activeConnections.remove(id);
    }

    public void addOnlinePlayer(Player player) {
        onlinePlayers.put(player.getId(), player);
        notifyLobbyListeners(player, LobbyData.LobbyAction.ADD_ONE);
        onlinePlayersObservers.forEach(
                (id, consumer) -> consumer.accept(player, PlayerAction.ADDED)
        );
    }

    public void updateOnlinePlayer(Player player) {
        onlinePlayers.put(player.getId(), player);
        notifyLobbyListeners(player, LobbyData.LobbyAction.ADD_ONE);
        onlinePlayersObservers.forEach(
                (id, consumer) -> consumer.accept(player, PlayerAction.UPDATED)
        );
    }

    public void removeOnlinePlayer(int pid) {
        Player player = onlinePlayers.remove(pid);
        notifyLobbyListeners(player, LobbyData.LobbyAction.REMOVE_ONE);
        onlinePlayersObservers.forEach(
                (id, consumer) -> consumer.accept(player, PlayerAction.REMOVED)
        );
    }

    public void clearOnlinePlayers() {
        onlinePlayers.clear();
        onlinePlayersObservers.forEach(
                (id, consumer) -> consumer.accept(new Player(), PlayerAction.CLEARED)
        );
    }

    public void addOnlinePlayersObserver(BiConsumer<Player, PlayerAction> consumer) {
        int observerId = observerIdGenerator.incrementAndGet();
        onlinePlayersObservers.put(observerId, consumer);
    }

    private void notifyLobbyListeners(Player player, LobbyData.LobbyAction lobbyAction) {

        LobbyData lobbyData = new LobbyData(-1, lobbyAction, player, List.of());
        String response = JsonUtils.toJson(lobbyData);

        lobbyListeners.forEach(id -> {
            XOClient client = getActiveConnection(id);
            if (client != null) {
                boolean success = client.sendToListener(EndPoint.LOBBY, response);
                if (!success) {
                    removeActiveConnection(id);
                    onlinePlayers.remove(id);
                    lobbyListeners.remove(id);
                }
            } else {
                lobbyListeners.remove(id);
            }
        });
    }

    public List<Player> getOnlinePlayers() {
        return onlinePlayers.values().stream().toList();
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

    public boolean isOnline(int id) {
        return onlinePlayers.containsKey(id);
    }
}
