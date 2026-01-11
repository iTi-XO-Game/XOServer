/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tornado.xoserver.server;

import com.tornado.xoserver.models.Challenge;
import com.tornado.xoserver.models.Challenge.ChallengeAction;
import com.tornado.xoserver.models.LobbyData;
import com.tornado.xoserver.models.LobbyData.LobbyAction;
import com.tornado.xoserver.models.LogoutRequest;
import com.tornado.xoserver.models.Player;
import java.util.List;
import java.util.Map;
import com.tornado.xoserver.models.*;
import com.tornado.xoserver.database.PlayerDAO;
import com.tornado.xoserver.database.GameHistoryDAO;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    
    private String processRequest(String requestJson, EndPoint endPoint) {
        String response = "";
        switch (endPoint) {
            case LOGIN -> {
                response = handleLogin(requestJson);
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
                //todo
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
                challengeListeners.add(challenge.getSender().getId());
                notifyLobbyListeners(challenge.getSender(), LobbyAction.ADD_ONE);

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
        LogoutRequest logoutRequest = JsonUtils.fromJson(request, LogoutRequest.class);

        Player player = logoutRequest.getPlayer();
        activeConnections.remove(player.getId());
        onlinePlayers.remove(player.getId());
        challengeListeners.remove(player.getId());
        lobbyListeners.remove(player.getId());
        notifyLobbyListeners(player, LobbyAction.REMOVE_ONE);
        
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
          
            onlinePlayers.put(p.getId(), p);
            activeConnections.put(p.getId(), client);
            notifyLobbyListeners(p.getId(), LobbyAction.ADD_ONE);
          
            return JsonUtils.toJson(authResponse);
        }
    }

    private String handleRegister(String requestJson) {
        AuthRequest registerRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = new PlayerDAO();
        if (playerDao.createPlayer(registerRequest.getUsername(), registerRequest.getPassword())) {
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

        System.out.println(response);

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
}
