package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainLine {
    public Map map;
    public String name;
    public HashMap<String, Station> stations = new HashMap<String, Station>();
    private final ArrayList<int[]> path = new ArrayList<int[]>();
    public ArrayList<Train> trains = new ArrayList<Train>();
    public TrainLine(String name, Map map) {
        this.map = map;
        this.name = name;
    }

    public void addStation(String coordString, Station s) {
        stations.put(coordString, s);
    }

    public void addTrain(int dir) {
        trains.add(new Train(this, dir));
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
        for (Train train : trains) {
            train.dispose();
        }
    }
}
