package com.carbon.game;

import com.badlogic.gdx.math.Vector2;
import org.xguzm.pathfinding.grid.GridCell;

import java.util.ArrayList;
import java.util.List;

abstract public class FreeRoam extends GridLogic implements Moving{
    public int cellX = 0;
    public int cellY = 0;

    //physical position for movement
    public Vector2 position = new Vector2(0,0);
    public Vector2 target = new Vector2(0,0);
    public Vector2 norm = new Vector2(0,0);
    protected final ArrayList<GridCell> path = new ArrayList<>();
    public boolean move = false;

    public FreeRoam(int x, int y) {
        setCell(x, y);
    }

    public void setCell(int cx, int cy) {
        cellX = cx;
        cellY = cy;
        position.set(v_cellToWorld(cx, cy));
        move = false;
    }

    //MOVEMENT FUNCTIONS
    public void arriveAtTarget() {
        //increase carbon
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
        path.addAll(list);
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
}
