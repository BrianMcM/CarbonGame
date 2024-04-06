package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.xguzm.pathfinding.grid.GridCell;

public class BikeStand {
    public final Player player;
    public GridCell cell;
    public Sound Error = Gdx.audio.newSound(Gdx.files.internal("SFX/Error.wav"));
    public Sound Bike_Get = Gdx.audio.newSound(Gdx.files.internal("SFX/bike_get.mp3"));

    public BikeStand(GridCell c, Player p) {
        cell = c;
        player = p;
    }

    public void select() {
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            Error.play();
            return;
        }
        activate();
    }

    public void activate() {
        //cant use in car
        if (player.mode == 3) {
            return;
        }
        System.out.println("bike");
        if (player.mode == 1) {
            player.mode = 2;
            Bike_Get.play();
        } else if (player.mode == 2) {
            player.mode = 1;
            Bike_Get.play();
        }

    }
    public void dispose() {
        Error.dispose();
        Bike_Get.dispose();
    }
}
