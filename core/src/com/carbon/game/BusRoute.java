package com.carbon.game;

import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusRoute {
    public Map map;
    public HashMap<String, BusStation> stations = new HashMap<String, BusStation>();
    private final ArrayList<int[]> path = new ArrayList<int[]>();
    public ArrayList<Bus> busses = new ArrayList<Bus>();

    public BusRoute(Map map) {
        this.map = map;
    }

    public void addStation(String coordString, BusStation bs) {
        stations.put(coordString, bs);
        bs.setRoute(this);
    }

    public void addBus(int stationIndex) {
        busses.add(new Bus(this, stationIndex));
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
        for (Bus bus : busses) {
            bus.dispose();
        }
    }

}
