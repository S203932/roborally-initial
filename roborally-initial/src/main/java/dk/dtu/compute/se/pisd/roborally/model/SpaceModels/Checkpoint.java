package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class Checkpoint extends Space {

    public final int number;

    public Checkpoint(Board board, int x, int y, Heading[] edges, int number) {
        super(board, x, y, edges);
        this.number = number;
        //TODO Auto-generated constructor stub
    }

}
