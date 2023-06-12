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

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;

public class ServerClient {
        public final String pong = "pong";
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

        public String getPong() {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/ping"))
                                .header("Content-Type", "application/json")
                                .build();

                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        String result = gson.fromJson(response.body(), String.class);
                        return result;
                } catch (Exception e) {
                        return null;
                }
        }

        public ArrayList<Lobby> getLobbies() {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/lobbies"))
                                .header("Content-Type", "application/json")
                                .build();

                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        ArrayList<Lobby> result = gson.fromJson(response.body(), new TypeToken<ArrayList<Lobby>>() {
                        }.getType());
                        return result;
                } catch (Exception e) {
                        return null;
                }
        }

        public ArrayList<Lobby> getSavedLobbies() {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/lobbies/saved"))
                                .header("Content-Type", "application/json")
                                .build();

                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        ArrayList<Lobby> result = gson.fromJson(response.body(), new TypeToken<ArrayList<Lobby>>() {
                        }.getType());
                        return result;
                } catch (Exception e) {
                        return null;
                }
        }

        public Lobby getLobby(int id) {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id))
                                .header("Content-Type", "application/json")
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        Lobby result = gson.fromJson(response.body(), Lobby.class);
                        return result;
                } catch (Exception e) {
                        return null;
                }

        }

        public boolean createLobby(Lobby lobby) {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby"))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .PUT(BodyPublishers.ofString(gson.toJson(lobby)))
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.body().equals(successful)) {
                                return true;
                        }
                        return false;
                } catch (Exception e) {
                        return false;
                }

        }

        public boolean saveLobbyGame(Lobby lobby) {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby/save"))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .PUT(BodyPublishers.ofString(gson.toJson(lobby)))
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.body().equals(successful)) {
                                return true;
                        }
                        return false;
                } catch (Exception e) {
                        return false;
                }

        }

        public boolean playerJoinLobby(int id, LobbyPlayer lobbyPlayer) {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .POST(BodyPublishers.ofString(gson.toJson(lobbyPlayer)))
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.body().equals(successful)) {
                                return true;
                        }
                        return false;
                } catch (Exception e) {
                        return false;
                }

        }

        public boolean deleteLobby(int id) {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .DELETE()
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.body().equals(successful)) {
                                return true;
                        }
                        return false;
                } catch (Exception e) {
                        return false;
                }

        }

        public boolean removePlayerFromLobby(int id, int playerId) {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id + "/" + playerId))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .DELETE()
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.body().equals(successful)) {
                                return true;
                        }
                        return false;
                } catch (Exception e) {
                        return false;
                }

        }

        public boolean updateBoard(int id, String board) {
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id + "/sync"))
                                .headers("Accept", "application/json", "Content-Type", "application/json")
                                .POST(BodyPublishers.ofString(board))
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.body().equals(successful)) {
                                return true;
                        }
                        return false;
                } catch (Exception e) {
                        return false;
                }
        }

        public Board getBoard(int id) {
                HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://" + address + ":8080/lobby/" + id + "/sync"))
                                .header("Content-Type", "application/json")
                                .build();
                try {
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        Board result = gson.fromJson(response.body(), Board.class);
                        return result;
                } catch (Exception e) {
                        return null;
                }

        }
}
