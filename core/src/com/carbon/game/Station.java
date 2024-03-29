package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class Station {
    private final Player player;
    public int type = 2;
    public GridCell cell;
    public boolean occupied = false;

    public Station(GridCell c, String layer, Player p) {
        cell = c;
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
        occupied = true;
        System.out.println("transit");
    }

    public void trainArrived(Train train) {
        player.hide = true;
        occupied = false;
    }

    public void playerExit() {
        player.setCell(cell.getX() - 1, cell.getY());
        player.hide = false;
    }
}
