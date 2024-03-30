package com.carbon.game;

import com.badlogic.gdx.math.Vector2;

public class GridLogic {
    int tileSize = 16;
    public int worldToCell(float num) {
        return (int) num/tileSize;
    }
    public float cellToWorld(int num) {
        return (float) num * tileSize;
    }

    public Vector2 v_cellToWorld(int x, int y) { return new Vector2(x,y).scl(tileSize); }
}
