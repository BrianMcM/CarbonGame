package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class TrainStation extends Station{
    private final Map map;
    public TrainLine line;
    public boolean occupied = false;

    public TrainStation(GridCell c, Player p, Map m) {
        super(c, p);
        map = m;
    }

    public void setLine(TrainLine line) {
        this.line = line;
    }

    public void activate() {
        //cant activate station on bike or in car
        if (player.mode != 1) {
            return;
        }
        line.addTrain();
        occupied = true;
        map.screen.inUseTile = new int[] {cell.getX(), cell.getY()};
        map.screen.metroVision = true;
    }

    public void trainArrived(Train train) {
        occupied = false;
        map.screen.inUseTile = null;
    }

    public void playerExit() {
        player.setCell(cell.getX() - 1, cell.getY());
        line.removeTrain();
        map.screen.metroVision = false;
    }
}
