package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class Station implements Inside, Selectable{
    public static Player player;
    private static Map map;
    public GridCell cell;
    public final boolean isTrain;
    public Route route;
    public boolean occupied = false;

    public Station(GridCell c, Player p, Map m, boolean t) {
        Station.player = p;
        Station.map = m;
        cell = c;
        isTrain = t;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void select() {
        //cant activate station on bike or in car
        if (player.mode != 1) {
            return;
        }
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            return;
        }
        playerEnter();
    }

    public void playerEnter() {
        occupied = true;
        player.hide = true;
        map.screen.building = new int[] {cell.getX(), cell.getY()};
        if (isTrain) {
            map.screen.metroVision = true;
        }
    }

    public void arrived() {
        occupied = false;
        map.screen.building = null;
    }

    public void playerExit() {
        player.setCell(cell.getX() - 1, cell.getY());
        player.exit();
    }
}
