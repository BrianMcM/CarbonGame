package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.Arrays;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.Timer;

public class Transit extends GridLogic implements Moving{
    public final boolean isTrain;
    public Station currentStation;
    private int pathIndex;
    public final Route route;
    public Vector2 position = new Vector2(0,0);
    public Vector2 target = new Vector2(0,0);
    public Vector2 norm = new Vector2(0,0);
    public final int buffer;
    public final float speed;
    boolean move = true;
    public int direction;
    public boolean letPlayerOff = false;

    public Texture img = new Texture(Gdx.files.internal("testShapes/circle.png"));

    public Transit(Route r, int index, boolean t, int d) {
        isTrain = t;
        route = r;
        pathIndex = index - d;
        direction = d;
        if (isTrain) {
            speed = 200;
            buffer = 4;
        } else {
            speed = 150;
            buffer = 3;
        }
        arriveAtTarget();
    }

    public void arriveAtTarget() {
        if (Route.map.player.transit == this) {
            Route.map.player.transitCost(isTrain);
        }
        pathIndex += direction;
        position.set(v_cellToWorld(route.getPath().get(pathIndex)[0], route.getPath().get(pathIndex)[1]));
        move = false;
        String coordString = Arrays.toString(route.getPath().get(pathIndex));
        //if at station
        if (route.stations.containsKey(coordString)) {
            currentStation = route.stations.get(coordString);
            checkForPlayer();
            return;
        }
        setTargets();
    }

    public void checkForPlayer() {
        if (letPlayerOff) {
            currentStation.playerExit();
            letPlayerOff = false;
            waitAtStation();
            return;
        }
        if (currentStation.occupied) {
            currentStation.arrived();
            Route.map.player.transit = this;
        }
        waitAtStation();
    }

    public void setTargets() {
        if ((pathIndex == 0 && direction == -1) || (pathIndex == route.getPath().size() - 1) && direction == 1) {
            resetPath();
            return;
        }
        target.set(v_cellToWorld(route.getPath().get(pathIndex + direction)[0], route.getPath().get(pathIndex + direction)[1]));
        norm.x = Float.compare(position.x, target.x);
        norm.y = Float.compare(position.y, target.y);
        //if diagonal reduce speed in half
        norm.nor();
        move = true;
    }

    public void resetPath(){ //circular
        if (direction == 1) {
            target.set(v_cellToWorld(route.getPath().get(0)[0], route.getPath().get(0)[1]));
            pathIndex = -1;
        } else {
            int size = route.getPath().size();
            target.set(v_cellToWorld(route.getPath().get(size - 1)[0], route.getPath().get(size - 1)[1]));
            pathIndex =  size;
        }
        norm.x = -direction;
        norm.y = 0;
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

    public void dispose() {
        img.dispose();
    }
}
