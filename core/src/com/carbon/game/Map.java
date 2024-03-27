package com.carbon.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import org.xguzm.pathfinding.gdxbridge.NavigationTiledMapLayer;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Map extends GridLogic{
    public TiledMap map;
    private final int width;
    private final int height;
    public NavigationTiledMapLayer gridLayer;
    public AStarGridFinder<GridCell> finder;
    public HashMap<GridCell, Station> stations = new HashMap<GridCell, Station>();

    public Map() {
        map = new TmxMapLoader().load("testMap/new.tmx");
        TiledMapTileLayer navLayer = (TiledMapTileLayer) map.getLayers().get("navigation");

        width = navLayer.getWidth();
        height = navLayer.getHeight();

        TiledMapTileLayer bikeStationLayer = (TiledMapTileLayer) map.getLayers().get("bikeStations");
        TiledMapTileLayer trainStationLayer = (TiledMapTileLayer) map.getLayers().get("trainStations");
        TiledMapTileLayer busStationLayer = (TiledMapTileLayer) map.getLayers().get("busStations");

        GridCell[][] grid = new GridCell[navLayer.getWidth()][navLayer.getHeight()];
        convertToGrid(navLayer, grid);
        convertToGrid(bikeStationLayer, grid);
        convertToGrid(trainStationLayer, grid);
        convertToGrid(busStationLayer, grid);

        finishGrid(grid);

        gridLayer = new NavigationTiledMapLayer(grid);
        finder = new AStarGridFinder<>(GridCell.class);
    }

    public void convertToGrid(TiledMapTileLayer layer, GridCell[][] grid) {
        boolean walkable = Objects.equals(layer.getName(), "navigation");
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //assigning walkable layers
                if (notNull(layer.getCell(x, y))) {
                    GridCell gc = new GridCell(x, y, walkable);
                    grid[x][y] = gc;
                    if (!Objects.equals(layer.getName(), "navLayer")) {
                        stations.put(gc, new Station(gc, layer.getName()));
                    }
                }
            }
        }
    }

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

    public List<GridCell> path(float startX, float startY, float endX, float endY) {
        return finder.findPath(worldToCell(startX), worldToCell(startY), worldToCell(endX), worldToCell(endY), gridLayer);
    }

    public void dispose() {
        map.dispose();
    }
}
