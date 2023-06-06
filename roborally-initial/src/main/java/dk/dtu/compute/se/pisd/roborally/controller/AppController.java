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

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.CourseModel.Course;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "brown", "orange", "yellow", "blue", "green");

    final private RoboRally roboRally;

    private GameController gameController;

    /**
     * <p>Constructor for AppController.</p>
     *
     * @param roboRally a {@link dk.dtu.compute.se.pisd.roborally.RoboRally} object.
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * <p>newGame.</p>
     */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (!result.isEmpty()) {
            // Load courses from json files
            ArrayList<String> courseNames = new ArrayList<String>();
            ArrayList<Course> jsonCourses = new ArrayList<Course>();

            try {
                File courses = new File("roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/courses");

                Gson gson = new Gson();
                Course jsonCourse = null;

                for (File course : courses.listFiles()) {
                    jsonCourse = gson.fromJson(new FileReader(course.getAbsolutePath()), Course.class);
                    courseNames.add(jsonCourse.game_name);
                    jsonCourses.add(jsonCourse);
                }

            } catch (JsonIOException e) {
                // TODO: handle exception

            } catch (FileNotFoundException e) {
                // TODO: handle exception
            }

            // Show dialog window
            ChoiceDialog<String> courseDialog = new ChoiceDialog<>(courseNames.get(0), courseNames);
            courseDialog.setTitle("Course");
            courseDialog.setHeaderText("Select a course");
            Optional<String> courseResult = courseDialog.showAndWait();

            if (!courseResult.isEmpty()) {
                Course selectedCourse = jsonCourses.get(courseNames.indexOf(courseResult.get()));

                Board board = new Board(selectedCourse);
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
        }
    }

    /**
     * <p>saveGame.</p>
     */
    public void saveGame() {
        // XXX needs to be implemented eventually
    }

    /**
     * <p>loadGame.</p>
     */
    public void loadGame() {
        // XXX needs to be implemented eventually
        // for now, we just create a new game
        if (gameController == null) {
            newGame();
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
            saveGame();

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

}
