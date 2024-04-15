package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import java.util.Arrays;

public class Transit extends GridLogic implements Moving{
    public boolean train;
    public boolean circular;
    public Station currentStation;
    private int pathIndex;
    public Route route;
    public Vector2 position = new Vector2(0,0);
    public Vector2 target = new Vector2(0,0);
    public Vector2 norm = new Vector2(0,0);
    public int buffer = 3;
    public float speed = (float) 150;
    boolean move = true;
    public int direction;
    public Texture img = new Texture(Gdx.files.internal("testShapes/bus_0003.png"));
    public boolean letPlayerOff = false;

    public Transit(Route r, int index, boolean t, boolean c, int d) {
        train =
        circular = c;
        route = r;
        pathIndex = index - d;
        direction = d;
        if (train) {
            speed = 200;
            buffer = 4;
        }
        arriveAtTarget();
    }

    public void arriveAtTarget() {
        if (route.map.player.transit == this) {
            route.map.player.transitCost(train);
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
            route.map.player.transit = this;
        }
        waitAtStation();
    }

    public void setTargets() {
        if ((pathIndex == 0 && direction == -1) || (pathIndex == route.getPath().size() - 1) && direction == 1) {
            if (circular) {
                resetPath();
                return;
            }
            direction *= -1;
        }
        target.set(v_cellToWorld(route.getPath().get(pathIndex + direction)[0], route.getPath().get(pathIndex + direction)[1]));
        norm.x = Float.compare(position.x, target.x);
        norm.y = Float.compare(position.y, target.y);
        //if diagonal reduce speed in half
        norm.nor();
        move = true;
    }

    public void resetPath(){ //circular
        target.set(v_cellToWorld(route.getPath().get(0)[0], route.getPath().get(0)[1]));
        pathIndex = -1;
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
