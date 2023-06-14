package dk.dtu.compute.se.pisd.roborallyServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.dtu.compute.se.pisd.roborallyServer.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyServer.model.LobbyPlayer;

@Service
public class ServerService implements IServerService {

    private final String pong = "pong";

    public HashMap<Integer, Lobby> lobbies = new HashMap<Integer, Lobby>();

    Gson gson = new GsonBuilder().create();

    @Override
    public String getPong() {
        return pong;
    }

    @Override
    public boolean playerJoinLobby(int id, LobbyPlayer player) {
        if (lobbies.get(id).getPlayer(player.getId()) != null) {
            return false;
        } else {
            return lobbies.get(id).addPlayer(player);
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
    public ArrayList<Lobby> getSavedLobbies() {

        ArrayList<Lobby> savedLobbies = new ArrayList<Lobby>();
        File savedLobbiesFile = new File("src/main/java/dk/dtu/compute/se/pisd/roborallyServer/savedGames");
        for (File savedLobbyFile : savedLobbiesFile.listFiles()) {
            try {
                savedLobbies.add(gson.fromJson(new FileReader(savedLobbyFile.getAbsolutePath()), Lobby.class));
            } catch (Exception e) {
                return null;
            }
        }
        return savedLobbies;
    }

    @Override
    public boolean saveLobbyGame(Lobby lobby) {
        // Remove players from save file
        if (lobbies.containsKey(lobby.getId())) {

            lobby.removePlayers();

            try {
                String lobbyFileName = lobby.getSaveId() + "-" + lobby.getName() + ".json";

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

    public boolean updateLobby(Lobby lobby) {
        if (lobbies.containsKey(lobby.getId())) {
            lobbies.put(lobby.getId(), lobby);
            return true;
        } else {
            return false;
        }
    }
}
