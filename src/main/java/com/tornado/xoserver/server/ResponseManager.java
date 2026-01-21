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

import javafx.application.Platform;

/**
 *
 * @author Hossam
 */
public class ResponseManager {

    private static final ResponseManager INSTANCE = new ResponseManager();

    private final ServerStateManager stateManager = ServerStateManager.getInstance();

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
                handleLogout(requestJson);
            }
            case LOBBY -> {
                response = handleLobby(requestJson);
            }
            case CHALLENGE -> {
                response = handleChallenge(requestJson);
            }
            case GAME -> {
                response = handleGame(requestJson);
            }
            case UPDATE_USER_PASS -> {
                response = handleForgetPassword(requestJson);
            }
            case PLAYER_GAMES_HISTORY -> {
                response = gameHistoryHandling(requestJson);
            }
            case OPPONENT_NAMES -> {
                response = getOpponentNamesHandling(requestJson);
            }
        }
        return response;
    }


    private String getOpponentNamesHandling(String request) {
        OpponentNamesRequest req = JsonUtils.fromJson(request, OpponentNamesRequest.class);

        List<Integer> opponentIds = req.getOpponentsIds();

        Map<Integer, String> usersMap = PlayerDAO.getUsernames(opponentIds);

        OpponentNamesResponse res = new OpponentNamesResponse(usersMap);

        return JsonUtils.toJson(res);
    }

    private String handleGame(String request) {
        ActiveGame activeGame = JsonUtils.fromJson(request, ActiveGame.class);

        switch (activeGame.getAction()) {
            case GameAction.LISTEN -> {
                stateManager.addPlayerInGame(activeGame.getSender().getId(), activeGame.getId());
                return startGame(activeGame);
            }
            case GameAction.STOP_LISTEN -> {

                Player sender = activeGame.getSender();
                stateManager.removePlayerInGame(sender.getId());
                sender.setPlaying(false);
                stateManager.updateOnlinePlayer(sender);

                Player receiver = activeGame.getReceiver();
                receiver.setPlaying(false);
                stateManager.updateOnlinePlayer(receiver);

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

                Player sender = activeGame.getSender();
                sender.setPlaying(false);
                stateManager.updateOnlinePlayer(sender);

                Player receiver = activeGame.getReceiver();
                receiver.setPlaying(false);
                stateManager.updateOnlinePlayer(receiver);

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

        XOClient client = stateManager.getActiveConnection(activeGame.getReceiver().getId());

        String response = JsonUtils.toJson(activeGame);

        boolean success = client.sendToListener(EndPoint.GAME, response);

        if (success) {
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
        PlayerDAO playerDAO = PlayerDAO.getInstance();

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

        ActiveGame activeGame = stateManager.getActiveGame(game.getId());
        game.setPlayerXid(game.getSender().getId());
        game.setPlayerOid(game.getReceiver().getId());
        game.setGameDate(System.currentTimeMillis());
        activeGame.setIsGameOn(true);

        String request = JsonUtils.toJson(activeGame);

        XOClient clientX = stateManager.getActiveConnection(activeGame.getPlayerXid());

        clientX.sendToListener(EndPoint.GAME, request);

        XOClient clientO = stateManager.getActiveConnection(activeGame.getPlayerOid());

        clientO.sendToListener(EndPoint.GAME, request);

        return request;
    }

    private String handleLobby(String request) {
        LobbyData oldData = JsonUtils.fromJson(request, LobbyData.class);

        switch (oldData.getLobbyAction()) {
            case LISTEN -> {
                stateManager.addLobbyListener(oldData.getSenderId());

                List<Player> players = stateManager.getOnlinePlayers();
                LobbyData newData = new LobbyData(
                        oldData.getSenderId(),
                        LobbyAction.LISTEN,
                        oldData.getUpdatedPlayer(),
                        players);

                return JsonUtils.toJson(newData);
            }
            case STOP_LISTEN -> {
                stateManager.removeLobbyListener(oldData.getSenderId());
            }
            case ERROR -> {
            }
        }

        return "";
    }

    private String handleChallenge(String request) {
        Challenge challenge = JsonUtils.fromJson(request, Challenge.class);

        switch (challenge.getAction()) {
            case LISTEN -> {
                //todo add feature to Start listening
            }
            case STOP_LISTEN -> {
                //todo add feature to Stop listening
            }
            case SEND, CANCEL, DECLINE -> {
                return getChallengeResponse(challenge, request);
            }
            case ACCEPT -> {
                String response = getChallengeResponse(challenge, request);
                if (response.isBlank()) {

                    ActiveGame game = challenge.toActiveGame();
                    stateManager.addActiveGame(challenge.getId(), game);

                    Player sender = challenge.getSender();
                    sender.setPlaying(true);
                    stateManager.updateOnlinePlayer(sender);

                    Player receiver = challenge.getReceiver();
                    receiver.setPlaying(true);
                    stateManager.updateOnlinePlayer(receiver);

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
        XOClient receiver = stateManager.getActiveConnection(receiverId);

        if (receiver != null) {
            boolean success = receiver.sendToListener(EndPoint.CHALLENGE, request);
            if (!success) {
                stateManager.removeActiveConnection(receiverId);
                stateManager.removeOnlinePlayer(receiverId);
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

    private void handleLogout(String request) {
        int pid;
        try {
            pid = Integer.parseInt(request);
        } catch (NumberFormatException e) {
            return;
        }

        stateManager.removeActiveConnection(pid);
        stateManager.removeLobbyListener(pid);
        stateManager.removeOnlinePlayer(pid);

        String gameId = stateManager.getPlayerInGame(pid);
        if (gameId != null) {
            ActiveGame game = stateManager.getActiveGame(gameId);
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
                    XOClient clientX = stateManager.getActiveConnection(rivalId);
                    if (clientX != null) {
                        String response = JsonUtils.toJson(game);
                        clientX.sendToListener(EndPoint.GAME, response);
                    }
                }
                stateManager.removeActiveGame(gameId);
                stateManager.removePlayerInGame(pid);
                stateManager.removePlayerInGame(rivalId);
                XOClient clientX = stateManager.getActiveConnection(rivalId);
                if (clientX != null) {
                    String response = JsonUtils.toJson(game);
                    clientX.sendToListener(EndPoint.GAME, response);
                }
            }
        }
    }

    // I know that this function may not be placed on the best place, but for now let's celebrate that it's actually working
    private String handleLogin(String requestJson, XOClient client) {
        AuthRequest loginRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = PlayerDAO.getInstance();
        Player p = playerDao.loginPlayer(loginRequest);

        if (p == null) {
            return JsonUtils.toJson(new AuthResponse(StatusCode.ERROR, "No User Found"));
        } else {
            if (stateManager.isOnline(p.getId())){
                return JsonUtils.toJson(new AuthResponse(StatusCode.ERROR, "User is already online"));
            }
            AuthResponse authResponse = new AuthResponse(StatusCode.SUCCESS, p.getId(), p.getUsername());

            authResponse.setPlayer(p);
            stateManager.addOnlinePlayer(p);
            stateManager.addActiveConnection(p.getId(), client);

            return JsonUtils.toJson(authResponse);
        }
    }

    private String handleRegister(String requestJson) {
        AuthRequest registerRequest = JsonUtils.fromJson(requestJson, AuthRequest.class);

        PlayerDAO playerDao = PlayerDAO.getInstance();
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

    public void sendExit() {
        AuthResponse response = new AuthResponse();
        response.setStatusCode(StatusCode.SERVER_CLOSED);
        String responseJson = JsonUtils.toJson(response);
        stateManager.broadcastToAllConnections(EndPoint.LOGIN, responseJson);
        stateManager.clearOnlinePlayers();
    }
}
