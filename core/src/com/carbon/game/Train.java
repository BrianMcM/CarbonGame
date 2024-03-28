package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Train extends GridLogic implements Moving{
    public Station currentStation;
    private int pathIndex;
    public TrainLine line;
    public float x = 0;
    public float y = 0;
    public float targetX = 0;
    public float targetY = 0;
    public float normX = 0;
    public float normY = 0;
    public int buffer = 5;
    public float speed = (float) 200;
    boolean move = true;
    public int direction;
    public Texture img = new Texture(Gdx.files.internal("testShapes/circle.png"));

    public Train(TrainLine l, int dir) {
        line = l;
        direction = dir;
        if (dir == 1) {
            pathIndex = 0;
        } else {
            pathIndex = line.stationList.size() - 1;
        }
        currentStation = line.stationList.get(pathIndex);
        arriveAtTarget();
    }

    public void arriveAtTarget() {
        pathIndex += direction;
        x = cellToWorld(line.getPath().get(pathIndex)[0]);
        y = cellToWorld(line.getPath().get(pathIndex)[1]);
        move = false;
        if (pathIndex == 0 || pathIndex == line.getPath().size() - 1) {
            direction *= -1;
            pathIndex += direction;
        }
        setTargets();
    }

    public void setTargets() {
        targetX = cellToWorld(line.getPath().get(pathIndex + direction)[0]);
        targetY = cellToWorld(line.getPath().get(pathIndex + direction)[1]);
        normX = Float.compare(x, targetX);
        normY = Float.compare(y, targetY);
        //if diagonal reduce speed in half
        if (normX != 0 && normY != 0) {
            normX /= 1.5F;
            normY /= 1.5F;
        }
        move = true;
    }

    public void atStation() {
        arriveAtTarget();
    }

    public void dispose() {
        img.dispose();
    }
}
