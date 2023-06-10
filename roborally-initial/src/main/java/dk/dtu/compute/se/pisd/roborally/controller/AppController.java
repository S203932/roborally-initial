/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.ServerClient;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Lobby;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.CourseModel.Course;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "saddlebrown", "orange", "yellow", "blue",
            "green");

    final private RoboRally roboRally;

    private GameController gameController;
    private ServerClient client;

    // Used for server lobbies
    private LobbyPlayer lobbyPlayer;
    private Lobby lobby;

    final private String CREATE_LOBBY = "Create a lobby";
    final private int LOBBY_HOST = 0;

    /**
     * <p>Constructor for AppController.</p>
     *
     * @param roboRally a {@link dk.dtu.compute.se.pisd.roborally.RoboRally} object.
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Dialog for connecting to a RoboRally server
     */
    public void connectServer() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connect to a RoboRally server");
        dialog.setHeaderText("Enter the server's address");

        Optional<String> result = dialog.showAndWait();

        if (!result.isEmpty() && result.get().length() > 0) {
            String pingResult = null;
            client = new ServerClient(result.get());

            pingResult = client.getPong();

            // Check if the result is as expected
            if (pingResult != null && pingResult.equals("pong")) {
                lobbyBrowser();
                return;
            }
            showAlert("Invalid server address", "Try again with a valid address");
        }

    }

    /**
     * Lobby browser for a RoboRally server
     */
    public void lobbyBrowser() {

        // Create player object
        // Get player name
        lobbyPlayer = new LobbyPlayer();
        Optional<String> result = null;

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Set name");
        nameDialog.setHeaderText("Enter your username (1-32 characters long)");

        result = nameDialog.showAndWait();
        if (result.isEmpty()) {
            return;
        } else if (!validName(result.get())) {
            showAlert("Invalid name", "Try again with a valid name");
            return;
        }

        lobbyPlayer.setName(result.get());

        // Show lobbies
        HashMap<String, Lobby> lobbies = refreshLobbies();
        ChoiceDialog<String> lobbyDialog = new ChoiceDialog<String>(CREATE_LOBBY,
                new ArrayList<String>(lobbies.keySet()));
        lobbyDialog.setTitle("Lobby browser");
        lobbyDialog.setHeaderText("Select or start a new lobby");
        result = lobbyDialog.showAndWait();

        if (!result.isEmpty()) {

            // Check if a new game should be made
            if (result.get().equals(CREATE_LOBBY)) {

                lobby = createLobby();
                if (lobby == null) {
                    return;
                }

                // Leave player ID to set to lobby host (0)
                client.createLobby(lobby);
            }

            // Try to join the selected game
            // Join the server with a valid unique player ID
            do {
                if (lobby == null) {
                    lobby = lobbies.get(result.get());
                }

                // Refresh server information
                lobby = client.getLobby(lobby.getId());

                // Check that the server is not full
                if (lobby.isFull()) {
                    showAlert("Game lobby is full", "Join another lobby");
                    break;
                }

                // Should be a unique ID
                lobbyPlayer.setId(lobby.getPlayersCount());

            } while (!client.playerJoinLobby(lobby.getId(), lobbyPlayer));

            waitingRoom();
        }
    }

    /**
     * Create a waiting room for a lobby. Host has the ability to start game and choose map.
     */
    private void waitingRoom() {
        if (lobbyPlayer.getId() == LOBBY_HOST) {
            waitingRoom(true);
        } else {
            waitingRoom(false);
            // waitingRoomNonHost();
        }

        // Start the game here upon button

    }

    private void waitingRoom(boolean host) {
        Course course = null;
        while (true) {
            // Create a custom Dialog that will return a string and an int
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(lobby.getName() + " lobby");
            if (host) {
                dialog.setHeaderText("Configure your game");
            } else {
                dialog.setHeaderText("Waiting for host to configure and start game");
            }

            ButtonType refreshLobbyButton = new ButtonType("Refresh lobby", ButtonData.LEFT);
            if (host) {
                ButtonType startGameButton = new ButtonType("Start game", ButtonData.OK_DONE);
                ButtonType deleteLobbyButton = new ButtonType("Delete lobby", ButtonData.CANCEL_CLOSE);
                ButtonType selectCourseButton = new ButtonType("Select course", ButtonData.OTHER);
                dialog.getDialogPane().getButtonTypes().addAll(startGameButton, deleteLobbyButton, selectCourseButton,
                        refreshLobbyButton);
            } else {
                ButtonType exitLobbyButton = new ButtonType("Exit lobby", ButtonData.RIGHT);
                dialog.getDialogPane().getButtonTypes().addAll(exitLobbyButton, refreshLobbyButton);
            }

            // Create grid for placing text fields
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));

            // Update lobby
            lobby = client.getLobby(lobby.getId());
            if (lobby == null) {
                showAlert("Invalid lobby", "The server host has closed the server");
                return;
            }

            // Add lobbyLabel
            String lobbyLabelString = "Currently joined players: " + lobby.getPlayersCount() + "/"
                    + lobby.getPlayerCount();
            if (course != null) {
                lobbyLabelString = lobbyLabelString
                        .concat("\nSelected course: " + course.getGameName() + "\nLength: " + course.getGameLength());
            }
            Label lobbyLabel = new Label(lobbyLabelString);
            lobbyLabel.setStyle("-fx-font-weight: bold");
            gridPane.add(lobbyLabel, 0, 0);

            // Add playerLabel
            String playerStrings = "";
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                playerStrings = playerStrings.concat(lobbyPlayer.getId() + " - " + lobbyPlayer.getName()
                        + (lobbyPlayer.getId() == LOBBY_HOST ? " (host)" : " (non-host)")
                        + (lobbyPlayer.getId() != lobby.getPlayerCount() - 1 ? "\n" : ""));
            }
            gridPane.add(new Label(playerStrings), 0, 1);

            // Add content to dialog
            dialog.getDialogPane().setContent(gridPane);

            Optional<ButtonType> result = dialog.showAndWait();

            if (!result.isEmpty()) {
                switch (result.get().getButtonData()) {
                    case OK_DONE:
                        if (course == null) {
                            showAlert("Select course", "Game cannot be started without a selecting a course");
                        } else if (lobby.getPlayersCount() == lobby.getPlayerCount()) {
                            System.out.println("starting game");
                            // TODO start game

                            //Creating the new board for the game
                            Board board = new Board(course);

                            //Stating that the game is online
                            board.setGameOnline(true);


                            // Adding the players from the lobby to the current board with names and id
                            int counter = 0;
                            for(LobbyPlayer lobbyPlayer : lobby.getPlayers()){
                                Player player = new Player(board,PLAYER_COLORS.get(counter),lobbyPlayer.getName(), lobbyPlayer.getId());
                                board.addPlayer(player);
                                counter++;
                            }

                            //Setting the first player as current player in game
                            board.setCurrentPlayer(board.getPlayer(0));


                            //Converting the board to a Json String
                            String boardString = boardToJson(board);

                            //Sending the board to the server
                            System.out.println("Status on sending to board to server:" + client.updateBoard(lobby.getId(), boardString));

                            //Receiving the board from the server as a JsonString
                            String receivedBoard = client.receiveBoard(lobby.getId());

                            //Loading the received boardString to a board and
                            // then initializing it the gamecontroller
                            loadBoard(receivedBoard);

                            roboRally.createBoardView(gameController);




                        } else {
                            showAlert("Not enough players", "The lobby does not have enough players to start.");





                        }
                        break;

                    case OTHER:
                        System.out.println("Selecting course");
                        course = selectCourse();
                        break;

                    case LEFT:
                        System.out.println("Refreshing");
                        System.out.println(client.saveLobbyGame(lobby));

                        break;

                    case RIGHT:
                        client.removePlayerFromLobby(lobby.getId(), lobbyPlayer.getId());
                        return;

                    case CANCEL_CLOSE:
                        client.deleteLobby(lobby.getId());
                        return;

                    default:
                        break;
                }

            }
        }
    }

    private String boardToJson(Board board){
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String boardString = gson.toJson(board);
        return boardString;
    }

    /**
     * Show a dialog for creating a lobby
     * @return 
     */
    private Lobby createLobby() {
        // Create a custom Dialog that will return a string and an int
        Dialog<Pair<String, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Create a lobby");
        dialog.setHeaderText("Customize your lobby");

        ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        // Create grid for placing text fields
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20, 150, 10, 10));

        // Add text field, choice box and labels
        TextField lobbyName = new TextField();

        ChoiceBox<Integer> lobbyPlayerCount = new ChoiceBox<Integer>();
        lobbyPlayerCount.getItems().addAll(PLAYER_NUMBER_OPTIONS);
        lobbyPlayerCount.setValue(PLAYER_NUMBER_OPTIONS.get(0));

        gridPane.add(new Label("Lobby name:"), 0, 0);
        gridPane.add(lobbyName, 1, 0);

        gridPane.add(new Label("Player count:"), 0, 1);
        gridPane.add(lobbyPlayerCount, 1, 1);

        // Add content to dialog
        dialog.getDialogPane().setContent(gridPane);

        // Ensure that the text field and choiceBox are returned as a result and not the buttons
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButton) {
                return new Pair<>(lobbyName.getText(), lobbyPlayerCount.getValue());
            }
            return null;
        });

        Optional<Pair<String, Integer>> result = dialog.showAndWait();

        // Ensure that the input is usable
        if (!result.isEmpty()) {
            if (validName(result.get().getKey())) {
                return new Lobby(result.get().getKey(), getLobbyId(), result.get().getValue());
            } else {
                showAlert("Invalid server configuration", "Try again with a valid configuration");
            }
        }

        return null;
    }

    private void showAlert(String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error dialog");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Get the first available unique ID from list of lobbies
     */
    private int getLobbyId() {
        ArrayList<Lobby> lobbies = new ArrayList<Lobby>();
        int lobbyId = 0;

        lobbies.addAll(client.getLobbies());

        for (int index = 0; index < lobbies.size(); index++) {
            if (lobbies.get(index).getId() == lobbyId) {
                lobbyId++;
                index = 0;
            }
        }
        return lobbyId;
    }

    private HashMap<String, Lobby> refreshLobbies() {

        HashMap<String, Lobby> lobbiesHashMap = new HashMap<String, Lobby>();

        ArrayList<Lobby> lobbies = client.getLobbies();

        // Add an empty option for creating a lobby
        lobbiesHashMap.put(CREATE_LOBBY, null);

        for (Lobby lobby : lobbies) {
            lobbiesHashMap.put("Lobby: " + lobby.getId() + " - " + lobby.getName() + " " + lobby.getPlayersCount()
                    + "/" + lobby.getPlayerCount() + " players", lobby);
        }

        return lobbiesHashMap;
    }

    /**
     * <p>newGame.</p>
     */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        // Select a course
        Course course = selectCourse();
        if (course == null) {
            return;
        }

        Board board = new Board(course);
        gameController = new GameController(board);
        int no = result.get();

        for (int i = 0; i < no; i++) {
            Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1), i + 1);
            board.addPlayer(player);
            player.setSpace(board.getStartGear(i));
        }

        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    private Course selectCourse() {
        // Load courses from json files
        ArrayList<String> courseNames = new ArrayList<String>();
        ArrayList<Course> jsonCourses = new ArrayList<Course>();

        try {
            File courses = new File("src/main/java/dk/dtu/compute/se/pisd/roborally/courses");

            Gson gson = new Gson();
            Course jsonCourse = null;

            for (File course : courses.listFiles()) {
                jsonCourse = gson.fromJson(new FileReader(course.getAbsolutePath()), Course.class);
                courseNames.add(jsonCourse.getGameName());
                jsonCourses.add(jsonCourse);

            }

        } catch (Exception e) {
            // Should not be any exceptions with valid JSON files
        }

        // Show dialog window
        ChoiceDialog<String> courseDialog = new ChoiceDialog<>(courseNames.get(0), courseNames);
        courseDialog.setTitle("Course");
        courseDialog.setHeaderText("Select a course");
        Optional<String> courseResult = courseDialog.showAndWait();

        if (!courseResult.isEmpty()) {
            Course selectedCourse = jsonCourses.get(courseNames.indexOf(courseResult.get()));
            return selectedCourse;
        }
        return null;
    }

    /***
     * Check if a name is valid for a lobby, saved game and player name
     * @param name
     * @return
     */
    private boolean validName(String name) {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9- æøåÆØÅ]{1,32}$");
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    /**
     * <p>saveGame.</p>
     */
    public void saveGame() {
        // XXX needs to be implemented eventually
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Save game");
        inputDialog.setContentText("Please state the name of the file to save to:");
        Optional<String> result = inputDialog.showAndWait();
        if (!(result.isEmpty()) && validName(result.get())) {
            String tempFilePath = "src/main/java/dk/dtu/compute/se/pisd/roborally/savedGames/";
            String newFileName = result.get() + ".json";

            try {
                FileWriter fileWriter = new FileWriter(tempFilePath + newFileName);
                gson.toJson(this.gameController.getBoard(), fileWriter);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Invalid name", "Choose a valid name to save the game");
        }

    }
    public void loadBoard(String board){
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        Board oldBoard = gson.fromJson(board, Board.class);

        String courseName = oldBoard.boardName;
        courseName = courseName.replace(" ", "_").toLowerCase();
        Course jsonCourse = null;


        try {
            File course = new File("src/main/java/dk/dtu/compute/se/pisd/roborally/courses/"+courseName+".json");

            Gson gson1 = new Gson();

            jsonCourse = gson1.fromJson(new FileReader(course.getAbsolutePath()), Course.class);


        } catch (JsonIOException e) {
            // TODO: handle exception

        } catch (FileNotFoundException e) {
            // TODO: handle exception
        }
        Board newBoard = new Board(jsonCourse);

        gameController = new GameController(newBoard);

        gameController.board.recreateBoardstate(oldBoard.getCurrentPlayer(),oldBoard.getPhase(),oldBoard.getPlayers(), oldBoard.getStep(),oldBoard.getStepmode());





    }

    /**
     * <p>loadGame.</p>
     */
    public void loadGame() {
        // XXX needs to be implemented eventually
        // for now, we just create a new game
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        String tempFilePath = "src/main/java/dk/dtu/compute/se/pisd/roborally/savedGames/";

        File files = new File(tempFilePath);
        File[] fileArray = files.listFiles();
        List<String> fileOptions = new ArrayList<String>();
        for (int i = 0; i < fileArray.length; i++) {
            if (fileArray[i].isFile()) {
                fileOptions.add(fileArray[i].getName());
            }
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(fileOptions.get(0), fileOptions);
        dialog.setTitle("Files");
        dialog.setHeaderText("Please select a file to load");
        Optional<String> fileName = dialog.showAndWait();
        File file = null;
        String boardString = null;
        if (fileName.isPresent()) {
            for (int i = 0; i < fileArray.length; i++) {
                if (fileArray[i].getName().equals(fileName.get())) {
                    file = fileArray[i];
                }
            }

            try {

                boardString = Files.readString(file.toPath());

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Board oldboard = gson.fromJson(boardString, Board.class);

            gameController = new GameController(oldboard);

            ArrayList<String> courseNames = new ArrayList<String>();
            ArrayList<Course> jsonCourses = new ArrayList<Course>();

            try {
                File courses = new File("src/main/java/dk/dtu/compute/se/pisd/roborally/courses");

                Gson gson1 = new Gson();
                Course jsonCourse = null;

                for (File course : courses.listFiles()) {
                    jsonCourse = gson1.fromJson(new FileReader(course.getAbsolutePath()), Course.class);
                    courseNames.add(jsonCourse.getGameName());
                    jsonCourses.add(jsonCourse);
                }

            } catch (JsonIOException e) {
                // TODO: handle exception

            } catch (FileNotFoundException e) {
                // TODO: handle exception
            }

            Course selectedCourse = jsonCourses.get(courseNames.indexOf(oldboard.boardName));

            Board board = new Board(selectedCourse);

            gameController = new GameController(board);

            gameController.board.recreateBoardstate(oldboard.getCurrentPlayer(), oldboard.getPhase(),
                    oldboard.getPlayers(), oldboard.getStep(), oldboard.getStepmode());

            roboRally.createBoardView(gameController);
        }

    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            //saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * <p>exit.</p>
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * <p>isGameRunning.</p>
     *
     * @return a boolean.
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    /** {@inheritDoc} */
    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    public GameController getGameController() {
        return this.gameController;
    }

}
