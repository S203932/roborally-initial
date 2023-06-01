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
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Space;
import dk.dtu.compute.se.pisd.roborally.model.SpaceModels.Wall;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
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
        //this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    public void updateWall() {
        // this.getChildren().clear();

        if (space instanceof Wall) {
            Wall wall = (Wall) space;
            boolean north = false;
            boolean south = false;
            boolean east = false;
            boolean west = false;

            for (Heading facing : wall.getFacing()) {
                if (facing == Heading.SOUTH) {
                    Image image = new Image(
                            "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallSouth.jpg");
                    ImagePattern imagePattern = new ImagePattern(image);
                    Rectangle rectangle = new Rectangle();
                    rectangle.setX(0.0f);
                    rectangle.setY(0.0f);
                    rectangle.setWidth(60.0f);
                    rectangle.setHeight(60.0f);
                    rectangle.setFill(imagePattern);
                    this.getChildren().add(rectangle);

                    south = true;

                } else if (facing == Heading.WEST) {
                    Image image = new Image(
                            "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallWest.jpg");
                    ImagePattern imagePattern = new ImagePattern(image);
                    Rectangle rectangle = new Rectangle();
                    rectangle.setX(0.0f);
                    rectangle.setY(0.0f);
                    rectangle.setWidth(60.0f);
                    rectangle.setHeight(60.0f);
                    rectangle.setFill(imagePattern);
                    this.getChildren().add(rectangle);

                    west = true;

                } else if (facing == Heading.NORTH) {
                    Image image = new Image(
                            "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallNorth.jpg");
                    ImagePattern imagePattern = new ImagePattern(image);
                    Rectangle rectangle = new Rectangle();
                    rectangle.setX(0.0f);
                    rectangle.setY(0.0f);
                    rectangle.setWidth(60.0f);
                    rectangle.setHeight(60.0f);
                    rectangle.setFill(imagePattern);
                    this.getChildren().add(rectangle);

                    north = true;

                } else if (facing == Heading.EAST) {
                    Image image = new Image(
                            "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallEast.jpg");
                    ImagePattern imagePattern = new ImagePattern(image);
                    Rectangle rectangle = new Rectangle();
                    rectangle.setX(0.0f);
                    rectangle.setY(0.0f);
                    rectangle.setWidth(60.0f);
                    rectangle.setHeight(60.0f);
                    rectangle.setFill(imagePattern);
                    this.getChildren().add(rectangle);

                    east = true;
                }
            }

            if (east && north) {
                Image image = new Image(
                        "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallEastNorth.jpg");
                ImagePattern imagePattern = new ImagePattern(image);
                Rectangle rectangle = new Rectangle();
                rectangle.setX(0.0f);
                rectangle.setY(0.0f);
                rectangle.setWidth(60.0f);
                rectangle.setHeight(60.0f);
                rectangle.setFill(imagePattern);
                this.getChildren().add(rectangle);
            } else if (north && west) {
                Image image = new Image(
                        "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallNorthWest.jpg");
                ImagePattern imagePattern = new ImagePattern(image);
                Rectangle rectangle = new Rectangle();
                rectangle.setX(0.0f);
                rectangle.setY(0.0f);
                rectangle.setWidth(60.0f);
                rectangle.setHeight(60.0f);
                rectangle.setFill(imagePattern);
                this.getChildren().add(rectangle);
            } else if (south && east) {
                Image image = new Image(
                        "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallSouthEast.jpg");
                ImagePattern imagePattern = new ImagePattern(image);
                Rectangle rectangle = new Rectangle();
                rectangle.setX(0.0f);
                rectangle.setY(0.0f);
                rectangle.setWidth(60.0f);
                rectangle.setHeight(60.0f);
                rectangle.setFill(imagePattern);
                this.getChildren().add(rectangle);
            } else if (west && south) {
                Image image = new Image(
                        "file:roborally-initial/src/main/java/dk/dtu/compute/se/pisd/roborally/image/WallWestSouth.jpg");
                ImagePattern imagePattern = new ImagePattern(image);
                Rectangle rectangle = new Rectangle();
                rectangle.setX(0.0f);
                rectangle.setY(0.0f);
                rectangle.setWidth(60.0f);
                rectangle.setHeight(60.0f);
                rectangle.setFill(imagePattern);
                this.getChildren().add(rectangle);
            }

        }

    }

    /** {@inheritDoc} */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        updateWall();
        if (subject == this.space) {
            updatePlayer();

        }
    }

}
