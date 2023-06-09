package dk.dtu.compute.se.pisd.roborallyServer.model;

public class LobbyPlayer {

    private String name;
    private int id = 0;

    public LobbyPlayer() {

    }

    public LobbyPlayer(String name) {
        this.name = name;
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
}