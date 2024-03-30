package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.Timer;

import static com.badlogic.gdx.utils.Timer.schedule;

public class Train extends GridLogic implements Moving{
    public TrainStation currentStation;
    private int pathIndex;
    public TrainLine line;
    public Vector2 position = new Vector2(0,0);
    public Vector2 target = new Vector2(0,0);
    public Vector2 norm = new Vector2(0,0);
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
        position.set(v_cellToWorld(line.getPath().get(pathIndex)[0], line.getPath().get(pathIndex)[1]));
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
            line.map.player.train = this;
        }
        waitAtStation();
        if (letPlayerOff) {
            letPlayerOff();
        }
    }

    public void setTargets() {
        target.set(v_cellToWorld(line.getPath().get(pathIndex + direction)[0], line.getPath().get(pathIndex + direction)[1]));
        norm.x = Float.compare(position.x, target.x);
        norm.y = Float.compare(position.y, target.y);
        //if diagonal reduce speed in half
        norm.nor();
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
