package com.carbon.game;

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
            return;
        }
        if (Math.abs(player.cellX - cell.getX()) > 1 || Math.abs(player.cellY - cell.getY()) > 1) {
            System.out.println("Too far");
            return;
        }
        playerEnter();
    }

    public void playerEnter() {
        occupied = true;
        player.hide = true;
        map.screen.building = new int[] {cell.getX(), cell.getY()};
        if (isTrain) {
            map.screen.metroVision = true;
            //Ambient train sounds
            Train_moving.loop(0.05f);
            Train_moving.stop();
        }
    }

    public void arrived() {
        occupied = false;
        map.screen.building = null;
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
