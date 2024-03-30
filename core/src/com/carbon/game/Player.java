package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.math.Vector2;
import org.xguzm.pathfinding.grid.GridCell;
import java.util.List;

public class Player extends GridLogic implements Moving{
    public GameScreen screen;
    //value variables
    public int carbon = 0;
    public int energy;
    private int gems = 0;
    //Cell position
    public int cellX = 0;
    public int cellY = 0;
    //physical position for movement
    public Vector2 position = new Vector2(0,0);
    public Vector2 target = new Vector2(0,0);
    public Vector2 norm = new Vector2(0,0);
    private List<GridCell> path;
    public boolean hide = false;

    //Movement Variables
    public int mode = 1; // 1-walking, 2-bike, 3-car
    //texture
    public Texture img = new Texture(Gdx.files.internal("testShapes/square.png"));
    public boolean move = false;
    public Transit transit = null;

    public Player(GameScreen screen, int e, int x, int y) {
        this.screen = screen;
        energy = e;
        setCell(x, y);
    }

    public void addGem() {
        gems += 1;
    }

    public void setCell(int cx, int cy) {
        cellX = cx;
        cellY = cy;
        position.set(v_cellToWorld(cx, cy));
        move = false;
    }

    //MOVEMENT FUNCTIONS
    public void arriveAtTarget() {
        cellX = pathFirst().getX();
        cellY = pathFirst().getY();
        position.set(target);
        move = false;
    }

    public void finishEarly() {
        GridCell temp = pathFirst();
        clearPath();
        path.add(temp);
    }

    public void setPath(List<GridCell> list) {
        path = list;
        setTargets();
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
        target.set(v_cellToWorld(pathFirst().getX(), pathFirst().getY()));
        norm.x = Integer.compare(cellX, pathFirst().getX());
        norm.y = Integer.compare(cellY, pathFirst().getY());
        norm.nor();
        move = true;
    }

    public void clearPath() {
        path.clear();
    }

    public GridCell pathFirst() {
        return path.get(0);
    }

    public void exit() {
        screen.metroVision = false;
        screen.building = null;
        hide = false;
        transit = null;
    }

    public void dispose() {
        img.dispose();
    }
}