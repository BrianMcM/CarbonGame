package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
    public final NavigationTiledMapLayer carGridLayer;
    public final AStarGridFinder<GridCell> finder;
    public HashMap<GridCell, String> stationList = new HashMap<>();
    public HashMap<GridCell, BikeStand> bikeStands = new HashMap<>();
    public HashMap<GridCell, Station> stations = new HashMap<>();
    public ArrayList<Route> routes = new ArrayList<>();
    public ArrayList<int[]> walkableTiles = new ArrayList<>();

    public ArrayList<int[]> drivableTiles = new ArrayList<>();
    public ArrayList<Car> cars = new ArrayList<>();
    public Music game_music = Gdx.audio.newMusic(Gdx.files.internal("SFX/Main_Music_City_Jazz.mp3"));

    public Map(GameScreen screen, Player player,String mapname,String metroname) {
        this.screen = screen;
        this.player = player;
        //game_music.setVolume(0.2f);
        //game_music.play();
        map = new TmxMapLoader().load("Maps/map_tutorial.tmx");
        metro = new TmxMapLoader().load("Maps/metro_tutorial.tmx");

//        map = new TmxMapLoader().load(mapname);
//        metro = new TmxMapLoader().load(metroname);
        TiledMapTileLayer navLayer = (TiledMapTileLayer) map.getLayers().get("navigation");

        width = navLayer.getWidth();
        height = navLayer.getHeight();

        TiledMapTileLayer bikeStationLayer = (TiledMapTileLayer) map.getLayers().get("bikeStation");
        TiledMapTileLayer trainStationLayer = (TiledMapTileLayer) map.getLayers().get("trainStation");
        TiledMapTileLayer busStationLayer = (TiledMapTileLayer) map.getLayers().get("busStation");

        GridCell[][] grid = new GridCell[width][height];
        convertToGrid(navLayer, grid);
        convertToGrid(bikeStationLayer, grid);
        convertToGrid(trainStationLayer, grid);
        convertToGrid(busStationLayer, grid);

        finishGrid(grid);
        gridLayer = new NavigationTiledMapLayer(grid);

        finder = new AStarGridFinder<>(GridCell.class);

        //train section
        TiledMapTileLayer trainLayer = (TiledMapTileLayer) map.getLayers().get("train");
        GridCell[][] trainLineGrid = new GridCell[width][height];
        convertToGridTransit(trainLayer, trainLineGrid);
        finishGrid(trainLineGrid);
        NavigationTiledMapLayer trainGridLayer = new NavigationTiledMapLayer(trainLineGrid);
        //hard code each train line
        //setTransitRoute(new int[]{45, 53}, new int[]{43, 53}, trainGridLayer, true, 1); //pink line
        //setTransitRoute(new int[]{77, 41}, new int[]{75, 41}, trainGridLayer, true, 1); //purple line
        //setTransitRoute(new int[]{13, 5}, new int[]{11, 5}, trainGridLayer, true, 1); //brown line
        //tutorial map trains
        setTransitRoute(new int[]{64, 54}, new int[]{62, 54}, trainGridLayer, true, 1);
        setTransitRoute(new int[]{40, 26}, new int[]{38, 26}, trainGridLayer, true,1);


        //bus section
        TiledMapTileLayer busLayer = (TiledMapTileLayer) map.getLayers().get("bus");
        GridCell[][] BusRouteGrid = new GridCell[width][height];
        convertToGridTransit(busLayer, BusRouteGrid);
        finishGrid(BusRouteGrid);
        NavigationTiledMapLayer busGridLayer = new NavigationTiledMapLayer(BusRouteGrid);
        //hard code bus routes
        //setTransitRoute(new int[]{13, 9}, new int[]{11, 9}, busGridLayer, false, 1);
        //setTransitRoute(new int[]{33, 29}, new int[]{31, 29}, busGridLayer, false, 1);
        //setTransitRoute(new int[]{93, 29}, new int[]{91, 29}, busGridLayer, false, 1);
        //setTransitRoute(new int[]{9, 49}, new int[]{7, 49}, busGridLayer, false, 1);
        //tutorial bus
        setTransitRoute(new int[]{50, 44}, new int[]{48, 44}, busGridLayer, false, 1);
        setTransitRoute(new int[]{48, 41}, new int[]{46, 41}, busGridLayer, false, 1);

        //cars
        TiledMapTileLayer carLayer = (TiledMapTileLayer) map.getLayers().get("car");
        GridCell[][] carGrid = new GridCell[width][height];
        convertToGridCar(carLayer, carGrid);
        finishGrid(carGrid);
        carGridLayer = new NavigationTiledMapLayer(carGrid);

        TiledMapTileLayer carSpawn = (TiledMapTileLayer) map.getLayers().get("carSpawn");
        spawnCars(carSpawn);
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
                    if (Objects.equals(layer.getName(), "bikeStation")) {
                        bikeStands.put(gc, new BikeStand(gc, player));
                        continue;
                    }
                    if (Objects.equals(layer.getName(), "trainStation")) {
                        stations.put(gc, new Station(gc, player, this, true));
                        continue;
                    }
                    if (Objects.equals(layer.getName(), "busStation")) {
                        stations.put(gc, new Station(gc, player, this, false));
                        System.out.println(gc);
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

    private void convertToGridCar(TiledMapTileLayer layer, GridCell[][] grid) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning movable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, true);
                    grid[x][y] = gc;
                    drivableTiles.add(new int[]{x, y});
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

    public List<GridCell> carPath(int startX, int startY, int endX, int endY) {
        return finder.findPath(startX, startY, endX, endY, carGridLayer);
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
        route.addTransit();
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
        boolean onRoad = false;
        for (int[] tile : drivableTiles) {
            if (tile[0] == player.cellX && tile[1] == player.cellY) {
                onRoad = true;
                break;
            }
        }
        if (!onRoad) {
            System.out.println("not on road.");
            return;
        }
        player.carCalled = true;
        int minPathSize = 9999; //bigger than game map so will always trigger
        Car nearestCar = null;
        for (Car car : cars) {
            if (car.cellX == player.cellX && car.cellY == player.cellY) {
                car.called = true;
                car.pickUpPlayer();
                return;
            }
            List<GridCell> cPath = carPath(car.cellX, car.cellY, player.cellX, player.cellY);
            if (cPath.size() >= minPathSize) {
                continue;
            }
            nearestCar = car;
            minPathSize = cPath.size();
        }
        assert nearestCar != null;
        nearestCar.called = true;
        nearestCar.resetPos();
        nearestCar.setPath(carPath(nearestCar.cellX, nearestCar.cellY, player.cellX, player.cellY));
    }

    public int[] randomTile() {
        Random random = new Random();
        int randomIndex = random.nextInt(drivableTiles.size() - 1);
        return(drivableTiles.get(randomIndex));
    }

    public void dispose() {
        map.dispose();
        metro.dispose();
        for (Route route : routes) {
            route.dispose();
        }
        for (Car car : cars) {
            car.dispose();
        }
        for (Station station : stations.values()) {
            station.dispose();
        }
    }
}
