package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import java.util.ArrayList;
import java.util.Collections;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;

public class Wall extends Space {

    public final Heading[] facing;

    public Wall(Board board, int x, int y, Heading[] edges, Heading[] facing) {
        super(board, x, y, edges);
        this.facing = facing;
        //TODO Auto-generated constructor stub
    }

    /** 
     * @return Heading[]
     */
    public Heading[] getFacing() {
        return facing;
    }

    /** 
     * Return a String containing the wall's directions sorted alphabetically
     * @return String
     */
    public String getFacingString() {
        ArrayList<String> list = new ArrayList<String>();

        for (Heading heading : facing) {
            list.add(heading.toString());
        }

        Collections.sort(list);
        return list.toString().replace(",", "").replace("[", "").replace("]", "").replace(" ", "");
    }

}
