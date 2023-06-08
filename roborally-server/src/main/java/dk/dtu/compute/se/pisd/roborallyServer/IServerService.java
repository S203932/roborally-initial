package dk.dtu.compute.se.pisd.roborallyServer;

import java.util.ArrayList;

import dk.dtu.compute.se.pisd.roborallyServer.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyServer.model.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborallyServer.model.State;

public interface IServerService {

    public String getPong();

    public State getState();

    public void putState(State state);

    public boolean playerJoinLobby(int id, LobbyPlayer player);

    public boolean createLobby(Lobby lobby);

    public boolean deleteLobby(int id);

    public ArrayList<Lobby> getLobbies();

}
