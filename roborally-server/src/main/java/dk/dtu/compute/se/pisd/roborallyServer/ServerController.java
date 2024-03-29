package dk.dtu.compute.se.pisd.roborallyServer;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.dtu.compute.se.pisd.roborallyServer.model.Lobby;
import dk.dtu.compute.se.pisd.roborallyServer.model.LobbyPlayer;

@RestController
public class ServerController {

    private final String successful = "Successful!";
    private final String unsuccessful = "Unsuccessful!";

    @Autowired
    private IServerService serverService;
    String response;

    @GetMapping(value = "/ping")
    public ResponseEntity<String> getPong() {
        String result = serverService.getPong();
        return ResponseEntity.ok().body(result);
    }

    @PutMapping(value = "/lobby/save")
    public ResponseEntity<String> saveLobbyGame(@RequestBody Lobby lobby) {
        if (serverService.saveLobbyGame(lobby)) {
            response = successful;
        } else {
            response = unsuccessful;
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/lobby/{id}")
    public ResponseEntity<String> playerJoinLobby(@PathVariable int id, @RequestBody LobbyPlayer player) {
        if (serverService.playerJoinLobby(id, player)) {
            response = successful;
        } else {
            response = unsuccessful;
        }
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/lobby/{id}/sync")
    public ResponseEntity<String> updateLobby(@PathVariable int id, @RequestBody Lobby lobby) {
        if (serverService.updateLobby(lobby)) {
            response = successful;
        } else {
            response = unsuccessful;
        }
        return ResponseEntity.ok().body(response);
    }

    @PutMapping(value = "/lobby")
    public ResponseEntity<String> createLobby(@RequestBody Lobby lobby) {
        if (serverService.createLobby(lobby)) {
            response = successful;
        } else {
            response = unsuccessful;
        }

        return ResponseEntity.ok().body(response);

    }

    @DeleteMapping(value = "/lobby/{id}")
    public ResponseEntity<String> deleteLobby(@PathVariable int id) {
        if (serverService.deleteLobby(id)) {
            response = successful;
        } else {
            response = unsuccessful;
        }

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/lobby/{id}/{playerId}")
    public ResponseEntity<String> removePlayerFromLobby(@PathVariable int id, @PathVariable int playerId) {
        if (serverService.removePlayerFromLobby(id, playerId)) {
            response = successful;
        } else {
            response = unsuccessful;
        }

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/lobby/{id}")
    public ResponseEntity<Lobby> getLobby(@PathVariable int id) {
        Lobby lobby = serverService.getLobby(id);
        return ResponseEntity.ok().body(lobby);
    }

    @GetMapping(value = "/lobbies")
    public ResponseEntity<ArrayList<Lobby>> getLobbies() {
        ArrayList<Lobby> lobbies = serverService.getLobbies();
        return ResponseEntity.ok().body(lobbies);
    }

    @GetMapping(value = "/lobby/{id}/sync")
    public ResponseEntity<String> getBoard(@PathVariable int id) {
        String board = serverService.getLobby(id).getBoardString();
        return ResponseEntity.ok().body(board);
    }

    @GetMapping(value = "/lobbies/saved")
    public ResponseEntity<ArrayList<Lobby>> getSavedLobbies() {
        ArrayList<Lobby> savedLobbies = serverService.getSavedLobbies();
        return ResponseEntity.ok().body(savedLobbies);
    }

}
