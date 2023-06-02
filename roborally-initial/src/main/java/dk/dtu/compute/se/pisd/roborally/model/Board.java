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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.CourseModel.Course;
import dk.dtu.compute.se.pisd.roborally.model.CourseModel.Tile;
import dk.dtu.compute.se.pisd.roborally.model.CourseModel.TileAttributes;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.BlueConveyor;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Energy;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.GreenConveyor;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.LaserStart;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.PriorityAntenna;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.RebootToken;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.StartGear;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Wall;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int boardCheckpoints = 0;

    public Board(Course course) {

        this.boardName = course.game_name;

        // Find width from number of elements in the first arraylist inside the main arraylist
        width = course.board.get(0).size();

        // Find height from number of arraylists
        height = course.board.size();

        spaces = new Space[width][height];
        Space space = null;

        TileAttributes defaultTileAttributes = new TileAttributes();
        defaultTileAttributes.edges = new Heading[0];

        // Generate the correct spaces:
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Tile tile = course.board.get(y).get(x);

                // Replace null attributes value with empty Heading array
                if (tile.attributes == null) {
                    tile.attributes = defaultTileAttributes;
                }

                // Create the correct space type
                switch (tile.type) {
                    case space:
                        space = new Space(this, x, y, tile.attributes.edges);
                        break;

                    case green_conveyor:
                        space = new GreenConveyor(this, x, y, tile.attributes.edges, tile.attributes.facing[0],
                                tile.attributes.turns);
                        break;

                    case blue_conveyor:
                        space = new BlueConveyor(this, x, y, tile.attributes.edges, tile.attributes.facing[0],
                                tile.attributes.turns);
                        break;

                    case energy:
                        space = new Energy(this, x, y, tile.attributes.edges);
                        break;

                    case start_gear:
                        space = new StartGear(this, x, y, tile.attributes.edges);
                        break;

                    case wall:
                        space = new Wall(this, x, y, tile.attributes.edges, tile.attributes.facing);
                        break;

                    case laser_start:
                        space = new LaserStart(this, x, y, tile.attributes.edges, tile.attributes.facing[0]);
                        break;

                    case reboot_token:
                        space = new RebootToken(this, x, y, tile.attributes.edges);
                        break;

                    case checkpoint:
                        space = new Checkpoint(this, x, y, tile.attributes.edges, tile.attributes.number);
                        setBoardCheckpoints(boardCheckpoints + 1);
                        break;
                    case priority_antenna:
                        space = new PriorityAntenna(this, x, y, tile.attributes.edges);
                        break;

                }

                // Store the created space
                spaces[x][y] = space;

            }
        }

        this.stepMode = false;
    }

    /**
     * <p>Getter for the field <code>gameId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * <p>Setter for the field <code>gameId</code>.</p>
     *
     * @param gameId a int.
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * <p>getSpace.</p>
     *
     * @param x a int.
     * @param y a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space} object.
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * <p>getPlayersNumber.</p>
     *
     * @return a int.
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * <p>addPlayer.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * <p>getPlayer.</p>
     *
     * @param i a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * <p>getCurrentPlayer.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * <p>setCurrentPlayer.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * <p>Getter for the field <code>phase</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Phase} object.
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * <p>Setter for the field <code>phase</code>.</p>
     *
     * @param phase a {@link dk.dtu.compute.se.pisd.roborally.model.Phase} object.
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * <p>Getter for the field <code>step</code>.</p>
     *
     * @return a int.
     */
    public int getStep() {
        return step;
    }

    /**
     * <p>Setter for the field <code>step</code>.</p>
     *
     * @param step a int.
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * <p>isStepMode.</p>
     *
     * @return a boolean.
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * <p>Setter for the field <code>stepMode</code>.</p>
     *
     * @param stepMode a boolean.
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * <p>getPlayerNumber.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     * @return a int.
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    /**
     * <p>getStatusMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        // XXX: V2 changed the status so that it shows the phase, the player and the step
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep()+
                ", Checkpoint:" + getCurrentPlayer().getCheckpointCount();
    }

    public int getBoardCheckpoints() {
        return boardCheckpoints;
    }

    public void setBoardCheckpoints(int boardCheckpoints) {
        this.boardCheckpoints = boardCheckpoints;
    }
}
