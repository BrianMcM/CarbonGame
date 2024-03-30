package com.carbon.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.xguzm.pathfinding.gdxbridge.NavigationTiledMapLayer;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.*;

public class Map extends GridLogic{
    public GameScreen screen;
    public Player player;
    public TiledMap map;
    public TiledMap metro;
    private final int width;
    private final int height;
    public NavigationTiledMapLayer gridLayer;
    public AStarGridFinder<GridCell> finder;
    public HashMap<GridCell, String> stationList = new HashMap<>();
    public HashMap<GridCell, TrainStation> trainStations = new HashMap<>();
    public HashMap<GridCell, BikeStation> bikeStations = new HashMap<>();
    public ArrayList<TrainLine> trainLines = new ArrayList<TrainLine>();

    public Map(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;

        map = new TmxMapLoader().load("testMap/new.tmx");
        metro = new TmxMapLoader().load("testMap/metro.tmx");
        TiledMapTileLayer navLayer = (TiledMapTileLayer) map.getLayers().get("navigation");

        width = navLayer.getWidth();
        height = navLayer.getHeight();

        TiledMapTileLayer bikeStationLayer = (TiledMapTileLayer) map.getLayers().get("bikeStations");
        TiledMapTileLayer trainStationLayer = (TiledMapTileLayer) map.getLayers().get("trainStations");
        TiledMapTileLayer busStationLayer = (TiledMapTileLayer) map.getLayers().get("busStations");

        GridCell[][] grid = new GridCell[width][height];
        convertToGrid(navLayer, grid);
        convertToGrid(bikeStationLayer, grid);
        convertToGrid(trainStationLayer, grid);
        //convertToGrid(busStationLayer, grid);

        finishGrid(grid);
        gridLayer = new NavigationTiledMapLayer(grid);

        finder = new AStarGridFinder<>(GridCell.class);

        //train section
        TiledMapTileLayer trainLayer = (TiledMapTileLayer) map.getLayers().get("lines");
        GridCell[][] trainLineGrid = new GridCell[width][height];
        convertToGridMetro(trainLayer, trainLineGrid);
        finishGrid(trainLineGrid);
        NavigationTiledMapLayer trainGridLayer = new NavigationTiledMapLayer(trainLineGrid);
        //hard code each train line
        setTrainLine(new int[]{8, 25}, new int[]{28, 20}, trainGridLayer);
    }

    public void convertToGrid(TiledMapTileLayer layer, GridCell[][] grid) {
        boolean walkable = Objects.equals(layer.getName(), "navigation");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning walkable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, walkable);
                    grid[x][y] = gc;
                    if (Objects.equals(layer.getName(), "navigation")) {
                        continue;
                    }
                    stationList.put(gc, layer.getName());
                    if (Objects.equals(layer.getName(), "bikeStations")) {
                        bikeStations.put(gc, new BikeStation(gc, player));
                        continue;
                    }
                    if (Objects.equals(layer.getName(), "trainStations")) {
                        trainStations.put(gc, new TrainStation(gc, player, this));
                        continue;
                    }
                    if (Objects.equals(layer.getName(), "busStations")) {
                        //bus code
                    }
                }
            }
        }
    }

    //redo this later
    public void convertToGridMetro(TiledMapTileLayer layer, GridCell[][] grid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning walkable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, true);
                    grid[x][y] = gc;
                }
            }
        }
    }
    //end redo

    public void finishGrid(GridCell[][] grid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid[x][y] == null) {
                    grid[x][y] =  new GridCell(x, y, false);
                }
            }
        }
    }

    private boolean notNull(TiledMapTileLayer.Cell cell) {
        return cell != null;
    }

    public List<GridCell> path(int startX, int startY, int endX, int endY) {

        return finder.findPath(startX, startY, endX, endY, gridLayer);
    }

    public void setTrainLine(int[] first, int[] last, NavigationTiledMapLayer layer) {
        TrainLine line = new TrainLine(this);
        trainLines.add(line);
        GridCell firstCell = gridLayer.getCell(first[0], first[1]);
        line.addStation(Arrays.toString(first), trainStations.get(firstCell));

        List<GridCell> linePath = finder.findPath(first[0], first[1], last[0], last[1], layer);
        line.setPath(first, linePath);
        for (GridCell gridCell : linePath) {
            int xCoord = gridCell.getX();
            int yCoord = gridCell.getY();
            GridCell gCell = gridLayer.getCell(xCoord, yCoord);
            if (trainStations.containsKey(gCell)) {
                line.addStation(Arrays.toString(new int[]{xCoord, yCoord}), trainStations.get(gCell));
            }
        }
    }

    public void dispose() {
        map.dispose();
        for (TrainLine line : trainLines) {
            line.dispose();
        }
    }
}
