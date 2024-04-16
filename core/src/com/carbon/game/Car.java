package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

public class Car extends FreeRoam {
    //constants
    public static final int CAB_CARBON = 10;
    private final Player player;
    private final Map map;
    //texture
    public Texture img = new Texture(Gdx.files.internal("testShapes/red_car_temp.png"));
    public boolean called = false;
    public boolean hidden = false;

    public Car(int x, int y, Player p, Map m) {
        super(x, y);
        player = p;
        map = m;
        drive();
    }

    public void resetPos() {
        setCell(cellX, cellY);
        move = false;
        path.clear();
    }

    public void arriveAtTarget() {
        super.arriveAtTarget();
        if (called) {
            Player.carbon += CAB_CARBON;
        }
    }

    protected void arrived() {
        if (called) {
            checkForPlayer();
        } else {
            Random random = new Random();
            int randomIndex = random.nextInt(5,10);
            park(randomIndex);
        }

    }

    private void checkForPlayer() {
        if (called && cellX == player.cellX && cellY == player.cellY) {
            pickUpPlayer();
        }
    }

    public void pickUpPlayer() {
        player.mode = 3;
        hidden = true;
        player.car = this;
        player.img = this.img;
    }
    public void dropOff() {
        setCell(player.cellX, player.cellY);
        hidden = false;
        player.car = null;
        park(3);
    }

    private void park(float time) {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run () {
                drive();
            }
        }, (float) time, 0, 0);
    }

    private void drive() {
        int [] dest = map.randomTile();
        setPath(map.path(cellX, cellY, dest[0], dest[1]));
    }

    public void dispose() {
        img.dispose();
    }
}
