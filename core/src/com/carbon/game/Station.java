package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class Station {
    private Player player;
    public int type;
    public GridCell cell;

    public Station(GridCell c, int type) {
        cell = c;
        this.type = type;
    }

    public void setPlayer (Player p) {
        player = p;
    }

    public void select() {
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            return;
        }
        System.out.println("bike");
        player.setMode(type);
    }
}
