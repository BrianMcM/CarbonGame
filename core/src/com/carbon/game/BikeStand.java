package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class BikeStand{
    public final Player player;
    public GridCell cell;
    public BikeStand(GridCell c, Player p) {
        cell = c;
        player = p;
    }

    public void select() {
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            return;
        }
        activate();
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
