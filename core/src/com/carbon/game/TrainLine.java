package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainLine {
    public Map map;
    public HashMap<String, TrainStation> stations = new HashMap<String, TrainStation>();
    private final ArrayList<int[]> path = new ArrayList<int[]>();
    public Train train = null;

    public TrainLine(Map map) {
        this.map = map;
    }

    public void addStation(String coordString, TrainStation ts) {
        stations.put(coordString, ts);
        ts.setLine(this);
    }

    public void addTrain(int dir) {
        if (train == null) {
            train = new Train(this, dir);
        }
    }
    public void removeTrain() {
        train.dispose();
        train = null;
    }

    public void setPath(int[] first, List<GridCell> p) {
        path.add(first);
        for (GridCell gc : p) {
            path.add(new int[]{gc.getX(), gc.getY()});
        }
    }

    public ArrayList<int[]> getPath() {
        return path;
    }

    public void dispose() {
        if (train != null) {
            train.dispose();
        }
    }

}
