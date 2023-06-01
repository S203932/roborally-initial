package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class Wall extends Space {

    public final Heading[] facing;

    public Wall(Board board, int x, int y, Heading[] edges, Heading[] facing) {
        super(board, x, y, edges);
        this.facing = facing;
        //TODO Auto-generated constructor stub
    }

    public Heading[] getFacing() {
        return facing;
    }

}
