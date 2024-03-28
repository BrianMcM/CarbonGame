package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.List;

public class TrainLine {
    String name;
    ArrayList<Station> stationList;
    List<GridCell> path;
    ArrayList<Train> trains = new ArrayList<Train>();
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

    public void setPath(List<GridCell> path) {
        this.path = path;
    }

    public void dispose() {
        for (Train train : trains) {
            train.dispose();
        }
    }
}
