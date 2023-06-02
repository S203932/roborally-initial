package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class Gear extends Space {

    public boolean clockwise;

    public Gear(Board board, int x, int y, Heading[] edges, boolean clockwise) {
        super(board, x, y, edges);
        this.clockwise = clockwise;
        //TODO Auto-generated constructor stub
    }
    public boolean getClockwise(){
        return clockwise;
    }
}