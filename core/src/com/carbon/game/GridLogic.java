package com.carbon.game;

import com.badlogic.gdx.math.Vector2;

public class GridLogic {
    public static final int TILE_SIZE = 16;
    public int worldToCell(float num) {
        return (int) num/TILE_SIZE;
    }
    public float cellToWorld(int num) {
        return (float) num * TILE_SIZE;
    }

    public Vector2 v_cellToWorld(int x, int y) { return new Vector2(x,y).scl(TILE_SIZE); }
}
