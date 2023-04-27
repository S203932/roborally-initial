package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class LaserStart extends Space {

    public final Heading[] facing;

    public LaserStart(Board board, int x, int y, Heading[] edges, Heading[] facing) {
        super(board, x, y, edges);
        this.facing = facing;
        //TODO Auto-generated constructor stub
    }

}
