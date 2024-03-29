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
    private final int width;
    private final int height;
    public NavigationTiledMapLayer gridLayer;
    public NavigationTiledMapLayer trainGridLayer;
    public AStarGridFinder<GridCell> finder;
    public HashMap<GridCell, Station> stations = new HashMap<>();
    public ArrayList<TrainLine> trainLines = new ArrayList<TrainLine>();

    public Map(GameScreen screen, Player player) {
        this.screen = screen;
        this.player = player;

        map = new TmxMapLoader().load("testMap/new.tmx");
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
        convertToGrid(busStationLayer, grid);

        finishGrid(grid);
        gridLayer = new NavigationTiledMapLayer(grid);

        TiledMapTileLayer trainLayer = (TiledMapTileLayer) map.getLayers().get("lines");
        GridCell[][] trainLineGrid = new GridCell[width][height];
        convertToGridTrain(trainLayer, trainLineGrid);
        finishGrid(trainLineGrid);
        trainGridLayer = new NavigationTiledMapLayer(trainLineGrid);

        finder = new AStarGridFinder<>(GridCell.class);

        //Hard code train lines here I think
        setTrainLine("Red Line", new int[]{8, 25}, new int[]{28, 20});
    }

    public void convertToGrid(TiledMapTileLayer layer, GridCell[][] grid) {
        boolean walkable = Objects.equals(layer.getName(), "navigation");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning walkable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, walkable);
                    grid[x][y] = gc;
                    if (!Objects.equals(layer.getName(), "navigation")) {
                        stations.put(gc, new Station(gc, layer.getName(), player));
                    }
                }
            }
        }
    }

    //redo this later
    public void convertToGridTrain(TiledMapTileLayer layer, GridCell[][] grid) {
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

    public void setTrainLine(String name, int[] first, int[] last) {
        TrainLine line = new TrainLine(name, this);
        trainLines.add(line);
        GridCell firstCell = gridLayer.getCell(first[0], first[1]);
        line.addStation(Arrays.toString(first), stations.get(firstCell));

        List<GridCell> linePath = finder.findPath(first[0], first[1], last[0], last[1], trainGridLayer);
        line.setPath(first, linePath);
        for (GridCell gridCell : linePath) {
            int xCoord = gridCell.getX();
            int yCoord = gridCell.getY();
            GridCell gCell = gridLayer.getCell(xCoord, yCoord);
            if (stations.containsKey(gCell)) {
                line.addStation(Arrays.toString(new int[]{xCoord, yCoord}), stations.get(gCell));
            }
        }
        line.addTrain(1);
    }

    public void dispose() {
        map.dispose();
        for (TrainLine line : trainLines) {
            line.dispose();
        }
    }
}
