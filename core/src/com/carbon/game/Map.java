package com.carbon.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.xguzm.pathfinding.gdxbridge.NavigationTiledMapLayer;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.*;

public class Map extends GridLogic{
    public final GameScreen screen;
    public final Player player;
    public final TiledMap map;
    public final TiledMap metro;
    private final int width;
    private final int height;
    public final NavigationTiledMapLayer gridLayer;
    public final AStarGridFinder<GridCell> finder;
    public HashMap<GridCell, String> stationList = new HashMap<>();
    public HashMap<GridCell, BikeStand> bikeStands = new HashMap<>();
    public HashMap<GridCell, Station> stations = new HashMap<>();
    public ArrayList<Route> routes = new ArrayList<>();
    public ArrayList<int[]> walkableTiles = new ArrayList<>();
    public ArrayList<Car> cars = new ArrayList<>();

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

        TiledMapTileLayer carLayer = (TiledMapTileLayer) map.getLayers().get("carLayer");

        GridCell[][] grid = new GridCell[width][height];
        convertToGrid(navLayer, grid);
        convertToGrid(bikeStationLayer, grid);
        convertToGrid(trainStationLayer, grid);
        convertToGrid(busStationLayer, grid);

        finishGrid(grid);
        gridLayer = new NavigationTiledMapLayer(grid);

        finder = new AStarGridFinder<>(GridCell.class);

        //train section
        TiledMapTileLayer trainLayer = (TiledMapTileLayer) map.getLayers().get("lines");
        GridCell[][] trainLineGrid = new GridCell[width][height];
        convertToGridTransit(trainLayer, trainLineGrid);
        finishGrid(trainLineGrid);
        NavigationTiledMapLayer trainGridLayer = new NavigationTiledMapLayer(trainLineGrid);
        //hard code each train line
        setTransitRoute(new int[]{8, 25}, new int[]{6, 25}, trainGridLayer, true, 1);

        //bus section
        TiledMapTileLayer busLayer = (TiledMapTileLayer) map.getLayers().get("busRoutes");
        GridCell[][] BusRouteGrid = new GridCell[width][height];
        convertToGridTransit(busLayer, BusRouteGrid);
        finishGrid(BusRouteGrid);
        NavigationTiledMapLayer busGridLayer = new NavigationTiledMapLayer(BusRouteGrid);
        //hard code bus routes
        setTransitRoute(new int[]{18,34}, new int[]{16,34}, busGridLayer, false, -1);

        //cars
        spawnCars(carLayer);

    }

    private void convertToGrid(TiledMapTileLayer layer, GridCell[][] grid) {
        boolean walkable = Objects.equals(layer.getName(), "navigation");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning walkable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, walkable);
                    grid[x][y] = gc;
                    if (Objects.equals(layer.getName(), "navigation")) {
                        walkableTiles.add(new int[]{x, y});
                    }
                    stationList.put(gc, layer.getName());
                    if (Objects.equals(layer.getName(), "bikeStations")) {
                        bikeStands.put(gc, new BikeStand(gc, player));
                        continue;
                    }
                    if (Objects.equals(layer.getName(), "trainStations")) {
                        stations.put(gc, new Station(gc, player, this, true));
                        continue;
                    }
                    if (Objects.equals(layer.getName(), "busStations")) {
                        stations.put(gc, new Station(gc, player, this, false));
                    }
                }
            }
        }
    }

    private void convertToGridTransit(TiledMapTileLayer layer, GridCell[][] grid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning movable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, true);
                    grid[x][y] = gc;
                }
            }
        }
    }

    private void finishGrid(GridCell[][] grid) {
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

    private void setTransitRoute(int[] first, int[] last, NavigationTiledMapLayer layer, boolean train, int dir) {
        Route route = new Route(this, train, dir);
        GridCell firstCell = gridLayer.getCell(first[0], first[1]);
        List<GridCell> linePath = finder.findPath(first[0], first[1], last[0], last[1], layer);
        route.setPath(first, linePath);

        routes.add(route);
        route.addStation(Arrays.toString(first), stations.get(firstCell));

        for (GridCell gridCell : linePath) {
            int xCoord = gridCell.getX();
            int yCoord = gridCell.getY();
            GridCell gCell = gridLayer.getCell(xCoord, yCoord);
            if (stations.containsKey(gCell)) {
                route.addStation(Arrays.toString(new int[]{xCoord, yCoord}), stations.get(gCell));
            }
        }
        route.addTransit(0);
    }

    private void spawnCars(TiledMapTileLayer layer) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (notNull(layer.getCell(x, y))) {
                    cars.add(new Car(x, y, player, this));
                }
            }
        }
    }

    public void callCar() {
        int minPathSize = 9999; //bigger than game map so will always trigger
        Car nearestCar = null;
        for (Car car : cars) {
            if (car.cellX == player.cellX && car.cellY == player.cellY) {
                car.called = true;
                car.pickUpPlayer();
                return;
            }
            List<GridCell> cPath = path(car.cellX, car.cellY, player.cellX, player.cellY);
            if (cPath.size() >= minPathSize) {
                continue;
            }
            nearestCar = car;
            minPathSize = cPath.size();
        }
        assert nearestCar != null;
        nearestCar.called = true;
        nearestCar.resetPos();
        nearestCar.setPath(path(nearestCar.cellX, nearestCar.cellY, player.cellX, player.cellY));
    }

    public int[] randomTile() {
        Random random = new Random();
        int randomIndex = random.nextInt(walkableTiles.size());
        return(walkableTiles.get(randomIndex - 1));
    }

    public void dispose() {
        map.dispose();
        metro.dispose();
        for (Route route : routes) {
            route.dispose();
        }
    }
}
