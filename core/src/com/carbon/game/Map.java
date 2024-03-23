package com.carbon.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import org.xguzm.pathfinding.gdxbridge.NavTmxMapLoader;
import org.xguzm.pathfinding.gdxbridge.NavigationTiledMapLayer;
import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;

import java.util.List;

public class Map {
    public TiledMap map;
    public NavigationTiledMapLayer navLayer;
    public AStarGridFinder<GridCell> finder;
    private final int tileSize = 16;

    public Map() {
        map = new NavTmxMapLoader().load("testMap/new.tmx");
        TiledMapTileLayer mapLayer = (TiledMapTileLayer) map.getLayers().get("navigation");

        navLayer = new NavigationTiledMapLayer(convertToGrid(mapLayer));
        finder = new AStarGridFinder<>(GridCell.class);//, options);
    }

    public GridCell[][] convertToGrid(TiledMapTileLayer tiledLayer) {

        int width = tiledLayer.getWidth();
        int height = tiledLayer.getHeight();

        GridCell[][] grid = new GridCell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean walkable = isWalkable(tiledLayer.getCell(x, y));
                grid[x][y] = new GridCell(x, y, walkable);
            }
        }
        return grid;
    }

    private boolean isWalkable(TiledMapTileLayer.Cell cell) {
        return cell != null;
    }
    public int worldToCell(float num) {
        return (int) num/tileSize;
    }
    public float cellToWorld(int num) {
        return (float) num * tileSize;
    }

    public List<GridCell> path(float startX, float startY, float endX, float endY) {
        return finder.findPath(worldToCell(startX), worldToCell(startY), worldToCell(endX), worldToCell(endY), navLayer);
    }

    public void dispose() {
        map.dispose();
    }
}