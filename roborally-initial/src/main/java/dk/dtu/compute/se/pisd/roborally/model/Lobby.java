package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;

public class Lobby {

    private String name;
    private int id;
    private ArrayList<LobbyPlayer> players = new ArrayList<LobbyPlayer>();
    private int playerCount = 6;

    public Lobby() {

    }

    public Lobby(String name, int id, int playerCount) {
        this.name = name;
        this.id = id;
        this.playerCount = playerCount;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LobbyPlayer> getPlayers() {
        return players;
    }

    public int getPlayersCount() {
        return players.size();
    }

    public boolean addPlayer(LobbyPlayer player) {
        // Ensure that not too many players get into the lobby
        if (players.size() == playerCount) {
            return false;
        }
        // Check if 
        players.add(player);
        return true;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public boolean isFull() {
        return players.size() == playerCount;
    }

}