package dk.dtu.compute.se.pisd.roborallyServer;

import java.util.ArrayList;

import dk.dtu.compute.se.pisd.roborallyServer.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyServer.model.LobbyPlayer;

public interface IServerService {

    public String getPong();

    public boolean saveLobbyGame(Lobby lobby);

    public boolean playerJoinLobby(int id, LobbyPlayer player);

    public boolean createLobby(Lobby lobby);

    public boolean deleteLobby(int id);

    public boolean removePlayerFromLobby(int id, int playerId);

    public Lobby getLobby(int id);

    public ArrayList<Lobby> getLobbies();

    public boolean updateLobby(Lobby lobby);

    public ArrayList<Lobby> getSavedLobbies();
}
