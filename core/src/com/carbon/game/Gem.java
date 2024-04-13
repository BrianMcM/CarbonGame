package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Gem extends GridLogic{
    GemSpawner spawner;
    public int value;
    public int[] cellCoords;
    public Texture img = new Texture(Gdx.files.internal("testShapes/gem4.png"));
    public Vector2 position;

    public Gem(int v, int[] c, GemSpawner gs) {
        spawner = gs;
        value = v;
        cellCoords = c;
        position = v_cellToWorld(c[0], c[1]);
    }

    public void dispose() {
        spawner.collected(cellCoords);
        img.dispose();
    }
}
