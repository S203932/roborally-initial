/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model.SpaceModels;

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @version $Id: $Id
 */
public class Space extends Subject {

    public final Board board;

    @Expose
    public final int x;
    @Expose
    public final int y;
    @Expose
    public final Heading[] edges;

    private Player player;

    /**
     * <p>Constructor for Space.</p>
     *
     * @param board a {@link dk.dtu.compute.se.pisd.roborally.model.Board} object.
     * @param x a int.
     * @param y a int.
     */
    public Space(Board board, int x, int y, Heading[] edges) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.edges = edges;
        player = null;
    }

    /**
     * <p>Getter for the field <code>player</code>.</p>
     *
     * @return a {@link dk.dtu.compute.se.pisd.roborally.model.Player} object.
     */
    public Player getPlayer() {
        return player;
    }

    /**  
     * <p>Setter fo r the field <code>playe r</code>.</p>
     *  
     * @param player a {@l ink dk.dtu.compute.se.pisd.roborally.model.Playe r} object.
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
