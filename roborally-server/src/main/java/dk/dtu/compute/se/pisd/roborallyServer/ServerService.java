package dk.dtu.compute.se.pisd.roborallyServer;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.dtu.compute.se.pisd.roborallyServer.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyServer.model.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborallyServer.model.State;

@Service
public class ServerService implements IServerService {

    private final String pong = "pong";

    public State state = new State("Initial");
    public HashMap<Integer, Lobby> lobbies = new HashMap<Integer, Lobby>();

    Gson gson = new GsonBuilder().create();

    @Override
    public String getPong() {
        return pong;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void putState(@RequestBody State state) {
        this.state = state;
    }

    @Override
    public boolean playerJoinLobby(int id, LobbyPlayer player) {
        if (lobbies.get(id).getPlayer(id) != null) {
            return false;
        } else {
            return lobbies.get(id).addPlayer(player);
        }
    }

    @Override
    public Boolean updateBoard(int id, String board){
        lobbies.get(id).setBoard(board);
        if(lobbies.get(id).getBoard() != null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean createLobby(Lobby lobby) {
        // Ensure that lobbies have a unique ID and reasonable name
        if (lobbies.containsKey(lobby.getId()) || lobby.getName().length() == 0 || lobby.getName().length() > 32) {
            return false;
        }
        lobbies.put(lobby.getId(), lobby);
        return true;
    }

    @Override
    public boolean deleteLobby(int id) {
        if (lobbies.containsKey(id)) {
            lobbies.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removePlayerFromLobby(int id, int playerId) {
        if (lobbies.containsKey(id)) {
            return lobbies.get(id).removePlayer(lobbies.get(id).getPlayer(playerId));
        }
        return false;
    }

    @Override
    public Lobby getLobby(int id) {
        return lobbies.get(id);
    }

    @Override
    public ArrayList<Lobby> getLobbies() {
        return new ArrayList<Lobby>(lobbies.values());
    }

    @Override
    public boolean saveLobbyGame(Lobby lobby) {
        if (lobbies.containsKey(lobby.getId())) {
            try {
                String lobbyFileName = lobby.getId() + "-" + lobby.getName() + ".json";

                FileWriter fileWriter = new FileWriter(
                        "src/main/java/dk/dtu/compute/se/pisd/roborallyServer/savedGames/" + lobbyFileName);
                gson.toJson(lobby, fileWriter);
                fileWriter.flush();
                fileWriter.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
