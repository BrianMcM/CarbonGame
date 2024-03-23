package com.carbon.game;

public class Station {
    private Player player;
    public int type;
    public int cellX;
    public int cellY;

    public Station(int x, int y, int type) {
        cellX = x;
        cellY = y;
        this.type = type;
    }

    public void setPlayer (Player p) {
        player = p;
    }

    public void select() {
        if (Math.abs(player.cellX - cellX) > 1 || Math.abs(player.cellY - cellY) > 1) {
            System.out.println("Too far");
            return;
        }
        System.out.println("bike");
        player.setMode(type);
    }
}
