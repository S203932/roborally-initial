package dk.dtu.compute.se.pisd.roborally;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.ArrayList;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;

public class ServerClient {
        private final String successful = "Successful!";
        private final String unsuccessful = "Unsuccessful!";

        private String address;

        public ServerClient(String address) {
                this.address = address;
        }

        private static final HttpClient httpClient = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

        private Gson gson = new GsonBuilder().create();

        public String getPong() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/ping"))
                                .header("Content-Type", "application/json")
                                .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                String result = gson.fromJson(response.body(), String.class);
                return result;
        }

        public ArrayList<Lobby> getLobbies() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/lobbies"))
                                .header("Content-Type", "application/json")
                                .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                ArrayList<Lobby> result = gson.fromJson(response.body(), new TypeToken<ArrayList<Lobby>>() {
                }.getType());
                return result;
        }

        public boolean createLobby(Lobby lobby) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby"))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .PUT(BodyPublishers.ofString(gson.toJson(lobby)))
                                .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.body().equals(successful)) {
                        return true;
                }
                return false;
        }

        public boolean playerJoinLobby(int id, LobbyPlayer lobbyPlayer) throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .POST(BodyPublishers.ofString(gson.toJson(lobbyPlayer)))
                                .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.body().equals(successful)) {
                        return true;
                }
                return false;
        }

}
