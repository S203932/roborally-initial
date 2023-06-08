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

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Gear;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Conveyor;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Wall;

import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class GameController {
    @Expose
    public Board board;

    /**
     * <p>Constructor for GameController.</p>
     *
     * @param board a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

        if (space != null && space.board == board) {
            Player currentPlayer = board.getCurrentPlayer();
            if (currentPlayer != null && space.getPlayer() == null) {
                currentPlayer.setSpace(space);
                int playerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
                board.setCurrentPlayer(board.getPlayer(playerNumber));
            }
        }

    }

    /**
     * <p>startProgrammingPhase.</p>
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /** 
     * @return CommandCard
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * <p>finishProgrammingPhase.</p>
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    public void startEndGamePhase(Player playerWon) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Highscore Table:");
        Player[] players = new Player[board.getPlayersNumber() + 1];
        for (int i = 0; i < players.length; i++) {
            players[i] = null;
        }
        //players[0] = board.getPlayer(0);
        int counter = 0;
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i) != null) {
                Player tempPlayer = board.getPlayer(i);
                counter = 0;
                while (counter <= board.getPlayersNumber()) {
                    if (players[counter] == null) {
                        players[counter] = tempPlayer;
                        break;
                    } else if (tempPlayer.getCheckpointCount() > players[counter].getCheckpointCount()) {
                        Player tempPlayer2 = players[counter];
                        players[counter] = tempPlayer;
                        tempPlayer = tempPlayer2;
                    }
                    counter++;
                }
            }
        }

        String scores = "";
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                scores = scores
                        + (i + 1 + ": " + players[i].getName() + "\t checkpoints: " + players[i].getCheckpointCount());
                scores = scores + "\n";
            }

        }
        alert.setContentText(scores);
        alert.show();

    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * <p>executePrograms.</p>
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * <p>executeStep.</p>
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: V2
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Check if a player should get a checkpoint and if a player has reached all checkpoints
     */
    private void checkCheckpoint() {
        // Check if a player should get checkpoint
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player tempPlayer = board.getPlayer(i);
            if (tempPlayer.getSpace() instanceof Checkpoint) {
                Checkpoint checkpoint = (Checkpoint) tempPlayer.getSpace();
                if (tempPlayer.getCheckpointCount() == (checkpoint.getNumber() - 1)) {
                    tempPlayer.setCheckpointCount(tempPlayer.getCheckpointCount() + 1);
                }
            }
        }

        // Check if any of the players have reached the last checkpoint
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player tempPlayer = board.getPlayer(i);
            if (tempPlayer.getCheckpointCount() == board.getBoardCheckpoints()) {
                board.setPhase(Phase.END_GAME);
                startEndGamePhase(tempPlayer);
            }
        }

    }

    /** 
     * Check if any players are on a conveyor and move them accordingly.
     * @param player
     */
    private void checkConveyor(Player player) {
        // Check if a player should be moved on a conveyor
        if (player.getSpace() instanceof Conveyor) {
            Conveyor conveyor = (Conveyor) player.getSpace();
            Conveyor previousConveyor = null;

            for (int i = 0; i < conveyor.getSpeed(); i++) {
                // Move player to the neighbor
                Space newSpace = board.getNeighbour(conveyor, conveyor.getFacing());
                player.setSpace(newSpace);

                // Check if the tile is still a conveyor and should still move the player
                if (player.getSpace() instanceof Conveyor) {
                    previousConveyor = conveyor;
                    conveyor = (Conveyor) player.getSpace();

                } else {
                    break;
                }
            }
            // Check if the player should be rotated 90 degrees in the direction of the conveyor's turn
            if (conveyor.shouldTurn() && conveyor.getFacing() != previousConveyor.getFacing()) {

                // Check which direction the player should be rotated 
                // This could probably be done more efficiently but is left as is for now
                int rotatePrev = 0;
                int rotateNext = 0;
                Heading tempFacing = previousConveyor.getFacing();

                while (tempFacing != conveyor.getFacing()) {
                    tempFacing = tempFacing.next();
                    rotateNext++;
                }

                tempFacing = previousConveyor.getFacing();

                while (tempFacing != conveyor.getFacing()) {
                    tempFacing = tempFacing.prev();
                    rotatePrev++;
                }

                // Rotate next direction
                if (rotateNext < rotatePrev) {
                    player.setHeading(player.getHeading().next());

                }
                // Rotate prev direction
                else {
                    player.setHeading(player.getHeading().prev());
                }
            }
        }
    }

    /**
     * Check if a player is on a gear and should be rotated.
     */
    private void checkGear() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i).getSpace() instanceof Gear) {
                Gear gear = (Gear) board.getPlayer(i).getSpace();
                gear.getClockwise();
                if (gear.getClockwise() == true) {
                    board.getPlayer(i).setHeading(board.getPlayer(i).getHeading().next());
                } else {
                    board.getPlayer(i).setHeading(board.getPlayer(i).getHeading().prev());
                }
            }
        }
    }

    /**
     * A method used for executing the cards in the activation phase. The method goes through a given card
     * and executes it for the given player.
     */

    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    if (card.command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    } else {
                        Command command = card.command;
                        executeCommand(currentPlayer, command);
                    }
                }

                if (step == 0) {
                    checkConveyor(currentPlayer);
                }

                checkCheckpoint();

                if (board.getPhase() != Phase.END_GAME) {
                    int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                    if (nextPlayerNumber < board.getPlayersNumber()) {
                        board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                    } else {
                        step++;
                        if (step < Player.NO_REGISTERS) {
                            makeProgramFieldsVisible(step);
                            board.setStep(step);
                            board.setCurrentPlayer(board.getPlayer(0));
                        } else {
                            startProgrammingPhase();
                        }
                    }
                }
                checkGear();

            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                /*case SPAM:
                    removeSpam(player);
                    break;

                 */
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    // TODO: V2
    /**
     * <p>moveForward.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void moveForward(@NotNull Player player) {
        Space space = player.getSpace();

        if (player != null && player.board == board && space != null) {
            Heading heading = player.getHeading();
            Heading newWallBlockHeading = heading.next().next();
            Space target = board.getNeighbour(space, heading);

            boolean wallBlock = false;

            if (target instanceof Wall) {
                Wall targetWall = (Wall) target;

                if (Arrays.asList(targetWall.getFacing()).contains(newWallBlockHeading)) {
                    wallBlock = true;
                }

            } else if (space instanceof Wall) {
                Wall wall = (Wall) space;

                if (Arrays.asList(wall.getFacing()).contains(heading)) {
                    wallBlock = true;
                }
            }

            // XXX note that this removes an other player from the space, when there
            //     is another player on the target. Eventually, this needs to be
            //     implemented in a way so that other players are pushed away!
            if (!wallBlock) {
                target.setPlayer(player);
            }
        }
    }

    // TODO: V2
    /**
     * <p>fastForward.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
    }

    // TODO: V2
    /**
     * <p>turnRight.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void turnRight(@NotNull Player player) {
        if (player != null && player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }

    // TODO: V2
    /**
     * <p>turnLeft.</p>
     *
     * @param player a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public void turnLeft(@NotNull Player player) {
        if (player != null && player.board == board) {
            player.setHeading(player.getHeading().prev());
        }
    }

    /**
     * <p>moveCards.</p>
     *
     * @param source a {@link dk.dtu.compute.se.pisd.roborally.model.CommandCardField} object.
     * @param target a {@link dk.dtu.compute.se.pisd.roborally.model.CommandCardField} object.
     * @return a boolean.
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

    /**
     * A method called when need to execute a command card, that has a variable option that needs a players choice
     * It sets the phase to Activation phase
     * @param command a {@link dk.dtu.compute.se.pisd.roborally.model.Command} object.
     */
    public void executeCommandOptionAndContinue(Command command) {
        board.setPhase(Phase.ACTIVATION);
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }

        if (!board.isStepMode() && board.getStep() < Player.NO_REGISTERS) {
            continuePrograms();
        }

    }

    public Board getBoard(){
        return this.board;
    }
    /*
    private void removeSpam(Player player) {
        player.getDmgcards().remove(Command.SPAM);
    }

     */
}
