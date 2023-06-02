package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import java.util.ArrayList;
import java.util.Collections;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

abstract public class Conveyor extends Space {

    public final Heading facing;
    public final Heading[] turns;

    public Conveyor(Board board, int x, int y, Heading[] edges, Heading facing, Heading[] turns) {
        super(board, x, y, edges);
        this.facing = facing;
        this.turns = turns;
        //TODO Auto-generated constructor stub
    }

    /** 
     * Return a String containing the conveyor's turn
     * @return String
     */
    public String getTurnsString() {
        ArrayList<String> list = new ArrayList<String>();

        for (Heading turn : turns) {
            list.add(turn.toString());
        }

        Collections.sort(list);
        return list.toString().replace(",", "").replace("[", "").replace("]", "").replace(" ", "") + facing.toString();
    }

}
