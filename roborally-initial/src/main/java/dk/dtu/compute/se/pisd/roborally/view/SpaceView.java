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
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Wall;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;


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

    //private WallView wallView = new WallView();


    /**
     * <p>Constructor for SpaceView.</p>
     *
     * @param space a {@link dk.dtu.compute.se.pisd.roborally.model.Space} object.
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
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(arrow);
        }
    }


    public void updateWall(){
        //this.getChildren().clear();
        /*
        Wall[] walls = space.getWalls();
        for(int i = 0; i < 4; i++){
            if(walls[i] != null && walls[i].getheading() == Heading.SOUTH){
                Rectangle rectangle = new Rectangle(0,0,0,20);
                Polygon polygon = new Polygon(0,10,0,20);


                polygon.getPoints().addAll(new Double[]{
                        200.0, 50.0,
                        400.0, 50.0,
                        450.0, 150.0,
                        400.0, 250.0,
                        200.0, 250.0,
                        150.0, 150.0,
                });
                polygon.setFill(Color.BLUE);

                rectangle.setFill(Color.ORANGE);
                this.getChildren().add(rectangle);
                this.getChildren().add(polygon);
            }
        }

         */
        /*
        Circle circle = new Circle(20);
        circle.setFill(Color.ORANGE);
        this.getChildren().add(circle);

         */
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
