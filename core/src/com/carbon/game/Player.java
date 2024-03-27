package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import org.xguzm.pathfinding.grid.GridCell;
import java.util.List;

public class Player {
    //value variables
    public int carbon = 0;
    public int energy;
    private int gems = 0;
    //Cell position
    public int cellX = 0;
    public int cellY = 0;
    //physical position for movement
    public float x = 0;
    public float y = 0;
    public float targetX = 0;
    public float targetY = 0;
    public float normX = 0;
    public float normY = 0;
    private List<GridCell> path;

    private final int tileSize = 16;
    //Movement Variables
    public int mode = 2; //speed = 50 * mode, tolerance = mode
    public int energyDrain;
    //texture
    public Texture img = new Texture(Gdx.files.internal("testShapes/square.png"));
    public boolean move = false;

    public Player(int e, int x, int y) {
        energy = e;
        setCell(x, y);
    }

    public void addGem() {
        gems += 1;
    }

    public void setCell(int cx, int cy) {
        cellX = cx;
        cellY = cy;
        x = cellToWorld(cx);
        y = cellToWorld(cy);
    }

    //MOVEMENT FUNCTIONS
    public void arriveAtTarget() {
        cellX = pathFirst().getX();
        cellY = pathFirst().getY();
        x = targetX;
        y = targetY;
        move = false;
    }

    public void finishEarly() {
        GridCell temp = pathFirst();
        clearPath();
        path.add(temp);
    }

    public void setPath(List<GridCell> list) {
        path = list;
    }

    public void nextCell() {
        arriveAtTarget();
        path.remove(0);
        if (path.isEmpty()) {
            return;
        }
        setTargets();
    }

    public void setTargets() {
        targetX = cellToWorld(pathFirst().getX());
        targetY = cellToWorld(pathFirst().getY());
        normX = Integer.compare(pathFirst().getX(), cellX) * -1;
        normY = Integer.compare(pathFirst().getY(), cellY) * -1;
        //if diagonal reduce speed in half
        if (normX != 0 && normY != 0) {
            normX /= 1.5F;
            normY /= 1.5F;
        }
        move = true;
    }

    public void clearPath() {
        path.clear();
    }

    public GridCell pathFirst() {
        return path.get(0);
    }

    //station interaction
    public void setMode(int num) {
        mode = num;
    }

    //UNIVERSAL FUNCTIONS
    public int worldToCell(float num) {
        return (int) num/tileSize;
    }
    public float cellToWorld(int num) {
        return (float) num * tileSize;
    }
    //DISPOSE
    public void dispose() {
        img.dispose();
    }
}