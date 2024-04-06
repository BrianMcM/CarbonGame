package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.xguzm.pathfinding.grid.GridCell;

public class Station implements Inside {
    public final Player player;
    public GridCell cell;
    private final Map map;
    public boolean train;
    public Route route;
    public boolean occupied = false;
    public Sound Error = Gdx.audio.newSound(Gdx.files.internal("SFX/Error.wav"));
    public Sound Metro_chime = Gdx.audio.newSound(Gdx.files.internal("SFX/metro_chime.wav"));
    public Sound Bus_Horn = Gdx.audio.newSound(Gdx.files.internal("SFX/Bus_Horn.wav"));
    public Sound Bus_Door = Gdx.audio.newSound(Gdx.files.internal("SFX/Bus_door.wav"));
    public Sound Train_moving = Gdx.audio.newSound(Gdx.files.internal("SFX/train_moving.wav"));
    public boolean bus_sounds = false;

    public Station(GridCell c, Player p, Map m, boolean t) {
        player = p;
        cell = c;
        map = m;
        train = t;
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
        map.screen.building = new int[]{cell.getX(), cell.getY()};
        if (!train) {
            Bus_Door.play();
        }
        if (train) {
            map.screen.metroVision = true;
            Metro_chime.play(0.1f);
            //Ambient train sounds
            Train_moving.play(0.1f);
        }
    }

    public void arrived() {
        occupied = false;
        map.screen.building = null;
        //Leaving bus stop
        if (!train) {
            Bus_Horn.play();
        }
        if (train) {
            Metro_chime.play();
        }
    }

    public void playerExit() {
        player.setCell(cell.getX() - 1, cell.getY());
        player.exit();
    }

    public void dispose() {
        Error.dispose();
        Metro_chime.dispose();
        Bus_Door.dispose();
        Train_moving.dispose();
        Bus_Horn.dispose();
    }
}
