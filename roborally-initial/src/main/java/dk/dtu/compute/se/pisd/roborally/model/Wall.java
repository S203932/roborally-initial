package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;

public class Wall {

    final private Heading heading;


    public Wall (@NotNull Heading heading){
       this.heading = heading;
    }

    public Heading getheading(){
        return heading;
    }


}
