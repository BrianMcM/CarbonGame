package com.carbon.game;

public class GridLogic {
    int tileSize = 16;
    public int worldToCell(float num) {
        return (int) num/tileSize;
    }
    public float cellToWorld(int num) {
        return (float) num * tileSize;
    }
}
