package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Car extends FreeRoam {
    //constants
    public static final int CAB_CARBON = 10;
    private final Player player;
    //texture
    public Texture img = new Texture(Gdx.files.internal("testShapes/circle.png"));
    public boolean called = false;
    public boolean hidden = false;

    public Car(int x, int y, Player p) {
        super(x, y);
        player = p;
    }

    public void arriveAtTarget() {
        super.arriveAtTarget();
        if (called) {
            checkForPlayer();
            Player.carbon += CAB_CARBON;
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
     }
}
