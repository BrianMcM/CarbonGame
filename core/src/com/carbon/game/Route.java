package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Route {
    public Map map;
    public boolean train;
    public HashMap<String, Station> stations = new HashMap<>();
    private final ArrayList<int[]> path = new ArrayList<>();
    public ArrayList<Transit> transitList = new ArrayList<>();

    public Route(Map map, boolean t) {
        train = t;
        this.map = map;
    }

    public void addStation(String coordString, Station s) {
        stations.put(coordString, s);
        s.setRoute(this);
    }

    public void addTransit(int stationIndex) {
        transitList.add(new Transit(this, stationIndex, train, !train, 1));
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
        for (Transit transit : transitList) {
            transit.dispose();
        }
    }
}
