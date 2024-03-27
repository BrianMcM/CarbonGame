package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class Station {
    private Player player;
    public int type = 2;
    public GridCell cell;

    public Station(GridCell c, String layer) {
        cell = c;
        //System.out.println(layer);
    }

    public void setPlayer (Player p) {
        player = p;
    }

    public void select() {
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            return;
        }
        if (type == 1) {
            bike();
        } else {
            transit();
        }
    }

    private void bike() {
        System.out.println("bike");
    }

    private void transit() {
        System.out.println("transit");
    }
}
