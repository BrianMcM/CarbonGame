package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

    public void addTrain() {
        if (train != null) {
            return;
        }
        int randomDir = new Random().nextBoolean() ? -1 : 1;
        int randomInt = (int) (Math.random() * stations.size());
        train = new Train(this, randomDir, randomInt);
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
