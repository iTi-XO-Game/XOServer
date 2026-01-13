/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import com.tornado.xoserver.models.Challenge;
import com.tornado.xoserver.models.Challenge.ChallengeAction;
import com.tornado.xoserver.models.LobbyData;
import com.tornado.xoserver.models.LobbyData.LobbyAction;
import com.tornado.xoserver.models.Player;

import java.util.List;
import java.util.Map;

import com.tornado.xoserver.models.*;
import com.tornado.xoserver.database.PlayerDAO;
import com.tornado.xoserver.database.GameHistoryDAO;
import com.tornado.xoserver.models.ActiveGame.GameAction;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Platform;

/**
 *
 * @author Hossam
 */
public class ResponseManager {

    private static final ResponseManager INSTANCE = new ResponseManager();

    private final Map<Integer, XOClient> activeConnections = new ConcurrentHashMap<>();
    private final Map<Integer, Player> onlinePlayers = new ConcurrentHashMap<>();
    private final Set<Integer> challengeListeners = ConcurrentHashMap.newKeySet();
    private final Set<Integer> lobbyListeners = ConcurrentHashMap.newKeySet();

    private final Map<String, ActiveGame> activeGames = new ConcurrentHashMap<>();
    private final Map<Integer, String> playersInGames = new ConcurrentHashMap<>();
    private final Set<Integer> gameListeners = ConcurrentHashMap.newKeySet();

    public static ResponseManager getInstance() {
        return INSTANCE;
    }

    private ResponseManager() {
    }

    public String getResponse(String request, XOClient client) {

        int firstSplit = request.indexOf("|");
        String endPointString = request.substring(0, firstSplit);

        EndPoint endPoint = EndPoint.fromString(endPointString);

        int secondSplit = request.indexOf("|", firstSplit + 1);
        String callbackId = request.substring(firstSplit + 1, secondSplit);

        String requestJson = request.substring(secondSplit + 1);

        return endPointString + "|" + callbackId + "|" + processRequest(requestJson, endPoint, client);
    }

    private String processRequest(String requestJson, EndPoint endPoint, XOClient client) {
        String response = "";
        switch (endPoint) {
            case LOGIN -> {
                response = handleLogin(requestJson, client);
            }
            case REGISTER -> {
                response = handleRegister(requestJson);
            }
            case LOGOUT -> {
                response = handleLogout(requestJson);
            }
            case LOBBY -> {
                response = handleLobby(requestJson);
            }
            case CHALLENGE -> {
                response = handleChallenge(requestJson, client);
            }
            case GAME -> {
                response = handleGame(requestJson, client);
            }
            case UPDATE_USER_PASS -> {
                response = handleForgetPassword(requestJson);
            }
            case PLAYER_GAMES_HISTORY -> {
                response = gameHistoryHandling(requestJson);
            }
        }
        return response;
    }

    private String handleGame(String request, XOClient senderClient) {
        ActiveGame activeGame = JsonUtils.fromJson(request, ActiveGame.class);

        switch (activeGame.getAction()) {
            case GameAction.LISTEN -> {
                playersInGames.put(activeGame.getSender().getId(), activeGame.getId());
                return startGame(activeGame);
            }
            case GameAction.STOP_LISTEN -> {

                playersInGames.remove(activeGame.getSender().getId());
                resetGamePlayerInLobby(activeGame.getSender());
                gameListeners.remove(activeGame.getSender().getId());
                gameListeners.remove(activeGame.getSender().getId());

                activeGame.getSender().setPlaying(false);
                onlinePlayers.put(activeGame.getSender().getId(), activeGame.getSender());
                notifyLobbyListeners(activeGame.getSender(), LobbyAction.ADD_ONE);

                if (activeGame.getIsGameOn()) {
                    activeGame.setAction(GameAction.GIVE_UP);
                    activeGame.setIsGameOn(false);
                }

                return forwardGame(activeGame);
            }
            case GameAction.START -> {
                startGame(activeGame);
            }
            case GameAction.RESTART -> {
                activeGame.reset();
                return forwardGame(activeGame);
            }
            case GameAction.GIVE_UP -> {
                saveGameToDatabase(activeGame, activeGame.getReceiver().getId());

                activeGame.getSender().setPlaying(false);
                onlinePlayers.put(activeGame.getSender().getId(), activeGame.getSender());
                notifyLobbyListeners(activeGame.getSender(), LobbyAction.ADD_ONE);

                activeGame.setErrorMessage("Opponent disconnected. You win!");
                return forwardGame(activeGame);
            }
            case GameAction.MOVE -> {
                if (!activeGame.getIsGameOn()) {
                    saveGameToDatabase(activeGame, activeGame.getWinnerId());
                }
                return forwardGame(activeGame);
            }
        }

        return "";
    }

    private String forwardGame(ActiveGame activeGame) {

        XOClient client = activeConnections.get(activeGame.getReceiver().getId());

        String response = JsonUtils.toJson(activeGame);

        boolean success = client.sendToListener(EndPoint.GAME, response);

        if (success || !activeGame.getIsGameOn()) {
            return "";
        }

        activeGame.setIsGameOn(false);
        ActiveGame errorGame = new ActiveGame(
                activeGame.getId(),
                GameAction.ERROR,
                activeGame.getReceiver(),
                activeGame.getSender(),
                activeGame.getSender().getUsername() + " has disconnected, so you win this one."
        );

        saveGameToDatabase(activeGame, activeGame.getSender().getId());

        response = JsonUtils.toJson(errorGame);

        return response;
    }

    private void saveGameToDatabase(ActiveGame activeGame, int winnerId) {
        GameHistoryDAO gameHistoryDAO = new GameHistoryDAO();
        PlayerDAO playerDAO = new PlayerDAO();

        Integer winner = winnerId;
        if (winner == -1) {
            playerDAO.incrementDraws(activeGame.getPlayerXid());
            playerDAO.incrementDraws(activeGame.getPlayerOid());
            winner = null;
        } else {

            playerDAO.incrementWins(winnerId);
            int loserId;
            if (winnerId == activeGame.getPlayerXid()) {
                loserId = activeGame.getPlayerOid();
            } else {
                loserId = activeGame.getPlayerXid();
            }
            playerDAO.incrementLosses(loserId);
        }

        GameHistory game = new GameHistory(
                activeGame.getPlayerXid(),
                activeGame.getPlayerOid(),
                winner,
                winnerId == -1,
                activeGame.getGameDate()
        );

        gameHistoryDAO.saveGame(game);

    }

    private String startGame(ActiveGame game) {

        ActiveGame activeGame = activeGames.get(game.getId());
        game.setPlayerXid(game.getSender().getId());
        game.setPlayerOid(game.getReceiver().getId());
        game.setGameDate(System.currentTimeMillis());
        activeGame.setIsGameOn(true);

        String request = JsonUtils.toJson(activeGame);

        XOClient clientX = activeConnections.get(activeGame.getPlayerXid());

        clientX.sendToListener(EndPoint.GAME, request);

        XOClient clientO = activeConnections.get(activeGame.getPlayerOid());

        clientO.sendToListener(EndPoint.GAME, request);

        return request;
    }

    private void resetGamePlayerInLobby(Player sender) {
        sender.setPlaying(false);
        onlinePlayers.put(sender.getId(), sender);

        if (onlinePlayers.containsKey(sender.getId())) {
            notifyLobbyListeners(sender, LobbyAction.ADD_ONE);
        } else {
            notifyLobbyListeners(sender, LobbyAction.REMOVE_ONE);
        }
    }

    private String handleLobby(String request) {
        LobbyData oldData = JsonUtils.fromJson(request, LobbyData.class);

        switch (oldData.getLobbyAction()) {
            case LISTEN -> {
                lobbyListeners.add(oldData.getSenderId());

                List<Player> players = getOnlineChallengers();
                LobbyData newData = new LobbyData(
                        oldData.getSenderId(),
                        LobbyAction.LISTEN,
                        oldData.getUpdatedPlayer(),
                        players);

                return JsonUtils.toJson(newData);
            }
            case STOP_LISTEN -> {
                lobbyListeners.remove(oldData.getSenderId());
            }
            case ERROR -> {
            }
        }

        return "";
    }

    private String handleChallenge(String request, XOClient senderClient) {
        Challenge challenge = JsonUtils.fromJson(request, Challenge.class);

        switch (challenge.getAction()) {
            case LISTEN -> {
                Player sender = challenge.getSender();
                activeConnections.put(sender.getId(), senderClient);
                challengeListeners.add(sender.getId());
                onlinePlayers.put(sender.getId(), sender);
                Platform.runLater(()->{
                    Stats.online.set(Stats.online.get() + 1);
                    Stats.allOnlinePlayers.add(sender.getUsername());

                    Stats.offline.set(Stats.offline.get() - 1);
                    Stats.allOfflinePlayers.remove(sender.getUsername());
                });
                notifyLobbyListeners(sender, LobbyAction.ADD_ONE);

                return request;
            }
            case STOP_LISTEN -> {
                int pid = challenge.getSender().getId();
                Player player = onlinePlayers.get(pid);
                challengeListeners.remove(pid);
                notifyLobbyListeners(player, LobbyAction.REMOVE_ONE);

                return request;
            }
            case SEND -> {
                return getChallengeResponse(challenge, request);
            }
            case CANCEL -> {
                return getChallengeResponse(challenge, request);
            }
            case DECLINE -> {
                return getChallengeResponse(challenge, request);
            }
            case ACCEPT -> {
                String response = getChallengeResponse(challenge, request);
                if (response.isBlank()) {

                    ActiveGame game = challenge.toActiveGame();
                    activeGames.put(challenge.getId(), game);

                    Player sender = challenge.getSender();
                    sender.setPlaying(true);
                    onlinePlayers.put(sender.getId(), sender);

                    Player receiver = challenge.getReceiver();
                    receiver.setPlaying(true);
                    onlinePlayers.put(receiver.getId(), receiver);

                    notifyLobbyListeners(sender, LobbyAction.ADD_ONE);
                    notifyLobbyListeners(receiver, LobbyAction.ADD_ONE);

                    Challenge chall = new Challenge(
                            challenge.getId(),
                            ChallengeAction.DONE,
                            challenge.getSender(),
                            challenge.getReceiver(),
                            ""
                    );
                    return JsonUtils.toJson(chall);
                } else {
                    return response;
                }
            }
            case ERROR -> {
            }
        }
        return "";
    }

    private String getChallengeResponse(Challenge challenge, String request) {

        int receiverId = challenge.getReceiver().getId();
        XOClient receiver = activeConnections.get(receiverId);

        if (receiver != null) {
            boolean success = receiver.sendToListener(EndPoint.CHALLENGE, request);
            if (!success) {
                activeConnections.remove(receiverId);
                onlinePlayers.remove(receiverId);
                Platform.runLater(()->{
                    Stats.online.set(Stats.online.get() - 1);
//                    Stats.allOnlinePlayers.add(sender.getUsername());
                });
                challengeListeners.remove(receiverId);
                Challenge response = new Challenge(
                        challenge.getId(),
                        ChallengeAction.ERROR,
                        challenge.getSender(),
                        challenge.getReceiver(),
                        challenge.getReceiver().getUsername() + " is disconnected"
                );
                return JsonUtils.toJson(response);
            }
        }
        return "";
    }

    private List<Player> getOnlineChallengers() {
        return challengeListeners.stream()
                .map(onlinePlayers::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private void notifyLobbyListeners(Player player, LobbyAction lobbyAction) {
        LobbyData lobbyData = new LobbyData(-1, lobbyAction, player, List.of());
        String response = JsonUtils.toJson(lobbyData);

        lobbyListeners.forEach(id -> {
            XOClient client = activeConnections.get(id);
            if (client != null) {
                boolean success = client.sendToListener(EndPoint.LOBBY, response);
                if (!success) {
                    activeConnections.remove(id);
                    onlinePlayers.remove(id);
                    challengeListeners.remove(id);
                    lobbyListeners.remove(id);
                }
            } else {
                lobbyListeners.remove(id);
            }
        });
    }

    private String handleLogout(String request) {
        int pid;
        try {
            pid = Integer.parseInt(request);
        } catch (NumberFormatException e) {
            return "";
        }

        activeConnections.remove(pid);
        challengeListeners.remove(pid);
        lobbyListeners.remove(pid);
        Player player = onlinePlayers.remove(pid);

        Platform.runLater(()->
        {
            Stats.allOnlinePlayers.remove(player.getUsername());
            Stats.online.set(Stats.online.get() - 1);

            Stats.allOfflinePlayers.add(player.getUsername());
            Stats.offline.set(Stats.offline.get() + 1);
        });

        if (player != null) {
            notifyLobbyListeners(player, LobbyAction.REMOVE_ONE);
        }
        String gameId = playersInGames.get(pid);
        if (gameId != null) {
            ActiveGame game = activeGames.get(gameId);
            if (game != null) {
                int rivalId = game.getPlayerXid() == pid ? game.getPlayerOid() : game.getPlayerXid();
                if (game.getIsGameOn()) {
                    saveGameToDatabase(game, rivalId);
                    game.setAction(GameAction.GIVE_UP);
                    game.setWinnerId(rivalId);
                    game.setIsGameOn(false);
                    game.setErrorMessage("Opponent disconnected. You win!");
                } else {
                    game.setAction(GameAction.STOP_LISTEN);
                    game.setErrorMessage("Opponent disconnected.");
                    XOClient clientX = activeConnections.get(rivalId);
                    if (clientX != null) {
                        String response = JsonUtils.toJson(game);
                        clientX.sendToListener(EndPoint.GAME, response);
                    }
                }
                activeGames.remove(gameId);
                playersInGames.remove(pid);
                playersInGames.remove(rivalId);
                XOClient clientX = activeConnections.get(rivalId);
                if (clientX != null) {
                    String response = JsonUtils.toJson(game);
                    clientX.sendToListener(EndPoint.GAME, response);
                }
            }
        }
        return "";
    }

    // I know that this function may not be placed on the best place, but for now let's celebrate that it's actually working
    private String handleLogin(String requestJson, XOClient client) {
        AuthRequest loginRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = new PlayerDAO();
        Player p = playerDao.loginPlayer(loginRequest);

        if (p == null) {
            return JsonUtils.toJson(new AuthResponse(StatusCode.ERROR, "No User Found"));
        } else {
            AuthResponse authResponse = new AuthResponse(StatusCode.SUCCESS, p.getId(), p.getUsername());

            authResponse.setPlayer(p);
            onlinePlayers.put(p.getId(), p);
            activeConnections.put(p.getId(), client);
            challengeListeners.add(p.getId());
            notifyLobbyListeners(p, LobbyAction.ADD_ONE);

            return JsonUtils.toJson(authResponse);
        }
    }

    private String handleRegister(String requestJson) {
        AuthRequest registerRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = new PlayerDAO();
        if (playerDao.createPlayer(registerRequest.getUsername(), registerRequest.getPassword())) {
            Platform.runLater(() -> {
                Stats.total.set(Stats.total.get() + 1);
                Stats.allPlayers.add(registerRequest.getUsername());
            });
            return JsonUtils.toJson(new AuthResponse(StatusCode.SUCCESS));
        } else {
            return JsonUtils.toJson(new AuthResponse(StatusCode.ERROR, "The User Name Already Exists"));
        }
    }

    private static String gameHistoryHandling(String requestJson) {
        GamesHistoryRequest request = JsonUtils.fromJson(requestJson, GamesHistoryRequest.class);

        GameHistoryDAO gameHistoryDao = new GameHistoryDAO();

        ArrayList<GameHistory> data = gameHistoryDao.getPlayerGames(request.getClientID());

        GamesHistoryResponse response = new GamesHistoryResponse(data);


        String temp = JsonUtils.toJson(response);

        return temp;
    }

    private String handleForgetPassword(String jsonRequest) {

        AuthRequest request = JsonUtils.fromJson(jsonRequest, AuthRequest.class);

        String username = request.getUsername();
        String pass = request.getPassword();

        Boolean resultOfUpdate = PlayerDAO.updataPlayerPass(username, pass);

        String response = JsonUtils.toJson(resultOfUpdate);

        return response;
    }

    public List<String> getOnlinePlayersName()
    {
        List<String> temp = new ArrayList<>();

        for (Player val : onlinePlayers.values())
            temp.add(val.getUsername());

        return temp;
    }
}
