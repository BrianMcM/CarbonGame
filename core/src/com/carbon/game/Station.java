package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.xguzm.pathfinding.grid.GridCell;

public class Station implements Inside, Selectable{
    public static Player player;
    private static Map map;
    public GridCell cell;
    public final boolean isTrain;
    public Route route;
    public boolean occupied = false;

    public Sound Error = Gdx.audio.newSound(Gdx.files.internal("SFX/Error.wav"));
    public Sound Metro_chime = Gdx.audio.newSound(Gdx.files.internal("SFX/metro_chime.wav"));
    public Sound Bus_Horn = Gdx.audio.newSound(Gdx.files.internal("SFX/Bus_Horn.wav"));
    public Sound Train_moving = Gdx.audio.newSound(Gdx.files.internal("SFX/train_moving.wav"));


    public Station(GridCell c, Player p, Map m, boolean t) {
        Station.player = p;
        Station.map = m;
        cell = c;
        isTrain = t;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void select() {
        //cant activate station on bike or in car
        if (player.mode != 1) {
            Error.play();
            return;
        }
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            Error.play();
            return;
        }
        playerEnter();
    }

    public void playerEnter() {
        occupied = true;
        player.hide = true;
        map.screen.stationInside = this;
        if (isTrain) {
            map.screen.metroVision = true;
            //Ambient train sounds
            Train_moving.loop(1f);
            Train_moving.stop();
        }
    }

    public void arrived() {
        if (isTrain) {
            occupied = false;
            Metro_chime.play(0.3f);
            map.screen.stationInside = null;
        } else {
            occupied = false;
            Bus_Horn.play();
            map.screen.stationInside = null;
        }
    }
    public void playerExit() {
        occupied = false;
        player.setCell(cell.getX() - 1, cell.getY());
        player.exit();
    }

    public void dispose() {
        Error.dispose();
        Metro_chime.dispose();
        Train_moving.dispose();
        Bus_Horn.dispose();
    }
}
