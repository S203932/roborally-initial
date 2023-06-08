package dk.dtu.compute.se.pisd.roborally.model;

import java.util.ArrayList;

public class Lobby {

    private String name;
    private int id;
    private ArrayList<Player> players = new ArrayList<Player>();
    private int maxPlayerCount = 6;

    public Lobby() {

    }

    public Lobby(String name, int id, int maxPlayerCount) {
        this.name = name;
        this.id = id;
        this.maxPlayerCount = maxPlayerCount;
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= maxPlayerCount) {
            return false;
        }
        players.add(player);
        return true;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

}
