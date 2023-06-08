package dk.dtu.compute.se.pisd.roborallyServer.model;

public class LobbyPlayer {

    private String name;
    private int id;

    public LobbyPlayer() {

    }

    public LobbyPlayer(String name, int id) {
        this.name = name;
        this.id = id;
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
