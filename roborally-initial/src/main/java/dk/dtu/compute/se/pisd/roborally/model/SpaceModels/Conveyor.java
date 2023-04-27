package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

abstract public class Conveyor extends Space {

    public final Heading[] facing;

    public Conveyor(Board board, int x, int y, Heading[] edges, Heading[] facing) {
        super(board, x, y, edges);
        this.facing = facing;
        //TODO Auto-generated constructor stub
    }

}
