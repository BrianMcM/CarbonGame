package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class BusStation extends Station{
    private final Map map;
    public BusRoute route;
    public boolean occupied = false;

    public BusStation(GridCell c, Player p, Map m) {
        super(c, p);
        map = m;
    }

    public void setRoute(BusRoute route) {
        this.route = route;
    }

    public void activate() {
        //cant activate station on bike or in car
        if (player.mode != 1) {
            return;
        }
        occupied = true;
        player.hide = true;
        map.screen.inUseTile = new int[] {cell.getX(), cell.getY()};
    }

    public void busArrived() {
        occupied = false;
        map.screen.inUseTile = null;
    }

    public void playerExit() {
        player.setCell(cell.getX() - 1, cell.getY());
        player.hide = false;
    }
}
