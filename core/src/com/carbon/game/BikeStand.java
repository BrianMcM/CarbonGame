package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.xguzm.pathfinding.grid.GridCell;

public class BikeStand implements Selectable{
    public Player player;
    public GridCell cell;
    public Sound Error = Gdx.audio.newSound(Gdx.files.internal("SFX/Error.wav"));
    public Sound Bell = Gdx.audio.newSound(Gdx.files.internal("SFX/bike_bell1.wav"));
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
        if (player.mode == 1) {
            Bell.play();
            player.onBike();
        } else if (player.mode == 2) {
            Bell.play();
            player.offBike();
        }

    }
}
