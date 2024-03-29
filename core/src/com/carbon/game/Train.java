package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Arrays;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.Timer;

import static com.badlogic.gdx.utils.Timer.schedule;

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
    public int buffer = 4;
    public float speed = (float) 200;
    boolean move = true;
    public int direction;
    public Texture img = new Texture(Gdx.files.internal("testShapes/circle.png"));
    public boolean letPlayerOff = false;

    public Train(TrainLine l, int dir) {
        line = l;
        direction = dir;
        if (dir == 1) {
            pathIndex = 0;
        } else {
            pathIndex = line.getPath().size() - 1;
        }
        arriveAtTarget();
    }

    public void arriveAtTarget() {
        pathIndex += direction;
        x = cellToWorld(line.getPath().get(pathIndex)[0]);
        y = cellToWorld(line.getPath().get(pathIndex)[1]);
        if (pathIndex == 0 || pathIndex == line.getPath().size() - 1) {
            direction *= -1;
        }
        move = false;
        String coordString = Arrays.toString(line.getPath().get(pathIndex));
        //if at station
        if (line.stations.containsKey(coordString)) {
            currentStation = line.stations.get(coordString);
            checkForPlayer();
        } else {setTargets();}
    }

    public void checkForPlayer() {
        if (currentStation.occupied) {
            currentStation.trainArrived(this);
            line.map.player.transit = this;
        }
        waitAtStation();
        if (letPlayerOff) {
            letPlayerOff();
        }
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

    private void waitAtStation(){
        Timer timer = new Timer();
        timer.scheduleTask(new Task() {
            @Override
            public void run () {
                setTargets();
            }
        }, 1, 0, 0);
    }

    public void letPlayerOff() {
        currentStation.playerExit();
        letPlayerOff = false;
    }

    public void dispose() {
        img.dispose();
    }
}
