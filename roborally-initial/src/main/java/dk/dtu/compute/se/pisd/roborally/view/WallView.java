/*
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Wall;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.jetbrains.annotations.NotNull;

public class WallView extends StackPane implements ViewObserver{
    //public static int SPACE_HEIGHT = 10; // 75;
    /** Constant <code>SPACE_WIDTH=60</code> */
    /*public static int SPACE_WIDTH = 10; // 75;

    public final Space space;

    public final Wall wall;

    public WallView(@NotNull Space space, Wall wall) {
        this.space = space;
        this.wall = wall;

        update(space);








        // This space view should listen to changes of the space
        space.attach(this);
        update(space);

    }

    public void drawWall(){
        Circle circle = new Circle(20);
        circle.setFill(Color.ORANGE);
        this.getChildren().add(circle);
    }


    private void updatePlayer() {
        this.getChildren().clear();

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

    @Override
    public void updateView(Subject subject) {
        //this.getChildren().clear();
        drawWall();
        if (subject == this.space) {
            updatePlayer();
        }
    }
}
*/



