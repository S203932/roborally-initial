package dk.dtu.compute.se.pisd.roborally.model.CourseModel;

import java.util.ArrayList;

public class Course {
    private String gameName;
    private String gameLength;
    private ArrayList<ArrayList<Tile>> board;

    public String getGameName() {
        return gameName;
    }

    public String getGameLength() {
        return gameLength;
    }

    public ArrayList<ArrayList<Tile>> getBoard() {
        return board;
    }
}
