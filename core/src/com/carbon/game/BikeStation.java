package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class BikeStation extends Station{
    public BikeStation(GridCell c, Player p) {
        super(c, p);
    }

    public void activate() {
        //cant use in car
        if (player.mode == 3) {
            return;
        }
        System.out.println("bike");
        if (player.mode == 1) {
            player.mode = 2;
        } else if (player.mode == 2) {
            player.mode = 1;
        }
    }
}
