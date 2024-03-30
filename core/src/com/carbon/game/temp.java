package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class temp {
    public final Player player;
    public GridCell cell;
    public temp(GridCell c, Player p) {
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
        return;
    }
}
