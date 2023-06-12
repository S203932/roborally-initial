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

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class Player extends Subject {

    /** Constant <code>NO_REGISTERS=5</code> */
    @Expose
    final public static int NO_REGISTERS = 5;
    /** Constant <code>NO_CARDS=8</code> */
    @Expose
    final public static int NO_CARDS = 8;

    public Board board;

    @Expose
    final private int id;
    @Expose
    private String name;
    @Expose
    private String color;
    @Expose
    private Space space;
    @Expose
    private Heading heading = SOUTH;
    @Expose
    private CommandCardField[] program;
    @Expose
    private CommandCardField[] cards;
    @Expose
    private final ArrayList<Command> damagecards;
    @Expose
    private int checkpointCount = 0;
    @Expose
    private Phase phase;

    /**
     * <p>Constructor for Player.</p>
     *
     * @param board a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
     * @param color a {@link java.lang.String} object.
     * @param name a {@link java.lang.String} object.
     */
    public Player(@NotNull Board board, String color, @NotNull String name, int id) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.id = id;

        this.space = null;

        this.damagecards = new ArrayList<>();

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public void setCards(CommandCardField[] cards) {
        for (int i = 0; i < cards.length; i++) {
            CommandCardField cardField = new CommandCardField(this);
            if (cards[i].getCard() != null) {
                CommandCard commandCard = new CommandCard(cards[i].getCard().command);
                cardField.setCard(commandCard);
            }
            cardField.setVisible(cards[i].isVisible());
            this.cards[i] = cardField;
        }
    }

    public void setProgram(CommandCardField[] program) {
        for (int i = 0; i < program.length; i++) {
            CommandCardField cardField = new CommandCardField(this);
            if (program[i].getCard() != null) {
                CommandCard commandCard = new CommandCard(program[i].getCard().command);
                cardField.setCard(commandCard);
            }
            cardField.setVisible(program[i].isVisible());
            this.program[i] = cardField;
        }
    }

    public CommandCardField[] getPrograms() {
        return this.program;
    }

    public CommandCardField[] getCards() {
        return this.cards;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public int getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>color</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getColor() {
        return color;
    }

    /**
     * <p>Setter for the field <code>color</code>.</p>
     *
     * @param color a {@link java.lang.String} object.
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * <p>Getter for the field <code>space</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space} object.
     */
    public Space getSpace() {
        return space;
    }

    /**
     * <p>Setter for the field <code>space</code>.</p>
     *
     * @param space a {@link dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space} object.
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * <p>Getter for the field <code>heading</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * <p>Setter for the field <code>heading</code>.</p>
     *
     * @param heading a {@link dk.dtu.compute.se.pisd.roborally.model.Heading} object.
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * <p>getProgramField.</p>
     *
     * @param i a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.CommandCardField} object.
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * <p>getCardField.</p>
     *
     * @param i a int.
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.CommandCardField} object.
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public void setDmgcards(Command card) {
        this.damagecards.add(card);
    }

    public ArrayList<Command> getDmgcards() {
        return this.damagecards;
    }

    public int getCheckpointCount() {
        return checkpointCount;
    }

    public void setCheckpointCount(int checkpointCount) {
        this.checkpointCount = checkpointCount;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
