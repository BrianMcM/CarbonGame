package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.List;

public class TrainLine {
    public String name;
    public ArrayList<Station> stationList;
    private final ArrayList<int[]> path = new ArrayList<int[]>();
    public ArrayList<Train> trains = new ArrayList<Train>();
    public TrainLine(String name) {
        this.name = name;
        stationList = new ArrayList<Station>();
    }

    public void addStation(Station s) {
        stationList.add(s);
    }

    public void addTrain(int dir) {
        trains.add(new Train(this, dir));
    }

    public Station nextStation(Station current) {
        int currentIndex = stationList.indexOf(current);
        if (currentIndex < stationList.size() - 1) {
            return stationList.get(currentIndex + 1);
        } else {
            return null;
        }
    }

    public void setPath(List<GridCell> p) {
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
