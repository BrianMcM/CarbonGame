package com.carbon.game;

import java.lang.Math;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final CarbonGame game;
    OrthographicCamera camera;
    //class objects
    Player player;
    //map
    OrthogonalTiledMapRenderer renderer;
    Map mapLoader;
    private final int tileSize = 16;
    //textures
    public Texture border = new Texture(Gdx.files.internal("border.png"));
    private float borderX = 0;
    private float borderY = 0;

    //Use constructor instead of create here
    public GameScreen(final CarbonGame game) {
        this.game = game;
        //load map
        mapLoader = new Map();

        float unitScale = 1f;
        renderer = new OrthogonalTiledMapRenderer(mapLoader.map, unitScale);
        //camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //player
        player = new Player(100, 5, 20);
        for (Station station : mapLoader.stations) {
            station.setPlayer(player);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        //render tile map
        renderer.render();
        camera.update();
        renderer.setView(camera);

        game.batch.setProjectionMatrix(camera.combined);
        //sprite batch
        game.batch.begin();
        //player sprite
        game.batch.draw(player.img, player.x, player.y, tileSize, tileSize);
        //cell selector for mouse
        game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
        game.batch.end();

        //track mouse movement for cell border
        borderX = cellToWorld(worldToCell(Gdx.input.getX())) - (float) tileSize /2; //find cell of where mouse is pointing
        //no idea why 56 works here
        borderY = cellToWorld(56 - worldToCell(Gdx.input.getY())) - (float) tileSize /2; // and return global position of the cell center

        //click input movement
        if (Gdx.input.justTouched()) {
            //end trip at next cell, take no other inputs
            if (player.move) {
                player.finishEarly();
                return;
            }
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            //check if clicked cell is walkable or station or button
            int touchedCellX = worldToCell(touchPos.x);
            int touchedCellY = worldToCell(touchPos.y);
            //if walkable start player movement
            if (mapLoader.navLayer.getCell(touchedCellX, touchedCellY).isWalkable()) {
                startPlayerPath(touchPos.x, touchPos.y);
                return;
            }
            for (Station station : mapLoader.stations) {
                if (station.cellX == touchedCellX && station.cellY == touchedCellY) {
                    station.select();
                }
            }
        }

        //player movement
        if (player.move) {
            player.x -= player.normX * player.mode * 50 * Gdx.graphics.getDeltaTime();
            player.y -= player.normY * player.mode * 50 * Gdx.graphics.getDeltaTime();
            if (Math.abs(player.x - player.targetX) < player.mode && Math.abs(player.y - player.targetY) < player.mode) {
                player.nextCell();
            }
        }
    }

    private void startPlayerPath(float tx, float ty) {
        player.setPath(mapLoader.path(player.x, player.y, tx, ty));
        player.setTargets();
    }

    public int worldToCell(float num) {
        return (int) num/tileSize;
    }
    public float cellToWorld(int num) {
        return (float) num * tileSize;
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        player.dispose();
        renderer.dispose();
        mapLoader.dispose();
    }
}
