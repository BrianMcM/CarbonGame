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
    public Sound Error = Gdx.audio.newSound(Gdx.files.internal("sfx/Error.wav"));
    public Sound Metro_chime = Gdx.audio.newSound(Gdx.files.internal("sfx/metro_chime.wav"));
    public Sound Bus_Horn = Gdx.audio.newSound(Gdx.files.internal("sfx/Bus_Horn.wav"));
    public Sound Train_moving = Gdx.audio.newSound(Gdx.files.internal("sfx/train_moving.wav"));

    public Station(GridCell c, Player p, Map m, boolean t) {
        player = p;
        cell = c;
        map = m;
        train = t;
        System.out.println(c);
        System.out.println(t);
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
        if (train) {
            map.screen.metroVision = true;
            //Ambient train sounds
            Train_moving.loop(0.05f);
            Train_moving.stop();
        }
    }

    public void arrived() {
        occupied = false;
        map.screen.building = null;
        //Leaving bus stop
        if (!train) {
            Bus_Horn.play(0.1f);
        }
        if (train) {
            Metro_chime.play(0.1f);
        }
    }

    public void playerExit() {
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
