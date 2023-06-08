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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.BlueConveyor;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Conveyor;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Energy;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Gear;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.GreenConveyor;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.LaserStart;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.PriorityAntenna;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.RebootToken;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.StartGear;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Wall;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class SpaceView extends StackPane implements ViewObserver {

    /** Constant <code>SPACE_HEIGHT=60</code> */
    final public static int SPACE_HEIGHT = 60; // 75;
    /** Constant <code>SPACE_WIDTH=60</code> */
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;

    /**
     * <p>Constructor for SpaceView.</p>
     *
     * @param space a {@link dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space} object.
     */
    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        Player player = space.getPlayer();

        if (player != null) {
            Image image = new Image(
                    "file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/Robot" + player.getId()
                            + ".png");
            ImagePattern imagePattern = new ImagePattern(image);
            Rectangle rectangle = new Rectangle();
            rectangle.setX(0.0f);
            rectangle.setY(0.0f);
            rectangle.setWidth(60.0f);
            rectangle.setHeight(60.0f);
            rectangle.setFill(imagePattern);
            rectangle.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(rectangle);
        }
    }

    /**
     * Add graphic to the conveyor belts on the board
     */
    public void updateConveyor() {

        // Ensure that the space is a conveyor
        if (space instanceof Conveyor) {

            Conveyor conveyor = (Conveyor) space;

            // Get the color
            String color = "";

            if (space instanceof BlueConveyor) {
                color = "Blue";
            } else if (space instanceof GreenConveyor) {
                color = "Green";
            }

            // Get the image based on direction and turns
            if (conveyor.getTurns() == null) {
                setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/"
                        + color + "Conveyor" + conveyor.getFacing() + ".jpg");

            } else {
                setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/"
                        + color + "Conveyor" + conveyor.getTurnsString() + ".jpg");
            }
        }

    }

    /**
     * Add graphic to the walls on the board
     */
    public void updateWall() {
        if (space instanceof Wall) {
            Wall wall = (Wall) space;
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/Wall"
                    + wall.getFacingString() + ".jpg");
        }
    }

    /**
     * Add graphic to the Space on the board
     */
    public void updateSpace() {
        if (space.getClass() == Space.class) {
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/Space.jpg");
        }
    }

    public void updateCheckpoint() {
        if (space instanceof Checkpoint) {
            Checkpoint checkpoint = (Checkpoint) space;
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/Checkpoint"
                    + checkpoint.getNumber() + ".jpg");
        }
    }

    public void updateStartGear() {
        if (space instanceof StartGear) {
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/StartGear.jpg");
        }
    }

    public void updateEnergyCube() {
        if (space instanceof Energy) {
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/EnergyCube.jpg");
        }
    }

    public void updateLaserStart() {
        if (space instanceof LaserStart) {
            LaserStart laserStart = (LaserStart) space;
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/LaserStart"
                    + laserStart.facing.toString() + ".jpg");
        }
    }

    public void updatePriorityAntenna() {
        if (space instanceof PriorityAntenna) {
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/PriorityAntenna.jpg");
        }
    }

    public void updateRebootToken() {
        if (space instanceof RebootToken) {
            setImage("file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/RebootToken.jpg");
        }
    }

    public void updateGear() {
        if (space instanceof Gear) {
            Gear gear = (Gear) space;
            if (gear.clockwise) {
                setImage(
                        "file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/GearClockwise.jpg");
            } else {
                setImage(
                        "file:src/main/java/dk/dtu/compute/se/pisd/roborally/image/GearCounterClockwise.jpg");
            }
        }
    }

    /** 
     * Wrap board image code in function for simplification
     * @param imagePath
     */
    private void setImage(String imagePath) {
        Image image = new Image(imagePath);
        ImagePattern imagePattern = new ImagePattern(image);
        Rectangle rectangle = new Rectangle();
        rectangle.setX(0.0f);
        rectangle.setY(0.0f);
        rectangle.setWidth(60.0f);
        rectangle.setHeight(60.0f);
        rectangle.setFill(imagePattern);
        this.getChildren().add(rectangle);
    }

    /** {@inheritDoc} */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        updateWall();
        updateCheckpoint();
        updateConveyor();
        updateSpace();
        updateStartGear();
        updateEnergyCube();
        updateLaserStart();
        updateRebootToken();
        updatePriorityAntenna();
        updateGear();
        if (subject == this.space) {
            updatePlayer();

        }
    }

}
