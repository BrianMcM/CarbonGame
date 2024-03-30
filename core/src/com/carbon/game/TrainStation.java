package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

public class TrainStation extends GridLogic{
    private final Player player;
    private final Map map;
    public GridCell cell;
    public TrainLine line;
    public boolean occupied = false;

    public TrainStation(GridCell c, Player p, Map m) {
        cell = c;
        player = p;
        map = m;
    }

    public void setLine(TrainLine line) {
        this.line = line;
    }

    public void select() {
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            return;
        }
        line.addTrain(1);
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
