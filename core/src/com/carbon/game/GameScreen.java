package com.carbon.game;

import java.lang.Math;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import org.xguzm.pathfinding.grid.GridCell;

public class GameScreen implements Screen {
    final CarbonGame game;
    OrthographicCamera camera;
    //class objects
    Player player;
    //map
    OrthogonalTiledMapRenderer mapRenderer;
    Map mapLoader;
    private final int tileSize = 16;
    //textures
    public Texture border = new Texture(Gdx.files.internal("border.png"));

    //Use constructor instead of create here
    public GameScreen(final CarbonGame game) {
        this.game = game;
        //load map
        mapLoader = new Map();

        float unitScale = 1f;
        mapRenderer = new OrthogonalTiledMapRenderer(mapLoader.map, unitScale);
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
        mapRenderer.render();
        camera.update();
        mapRenderer.setView(camera);

        //track mouse movement for cell border
        int mouseCellX = worldToCell(Gdx.input.getX());
        int mouseCellY = worldToCell(Gdx.input.getY());
        GridCell mouseCell = mapLoader.navLayer.getCell(mouseCellX, 56 - mouseCellY);

        float borderX = cellToWorld(mouseCellX) - (float) tileSize /2; //find cell of where mouse is pointing
        //no idea why 56 works here
        float borderY = cellToWorld(56 - mouseCellY) - (float) tileSize /2; // and return global position of the cell center
        //cell your mouse is on


        game.batch.setProjectionMatrix(camera.combined);
        //sprite batch
        game.batch.begin();
        //player sprite
        game.batch.draw(player.img, player.x, player.y, tileSize, tileSize);
        //cell selector for mouse
        if (!player.move) {
            if (mouseCell != null) {
                if (mouseCell.isWalkable()) {
                    game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                } else if (mapLoader.stations.contains()){

                }
            }
        }
        game.batch.end();

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
            //if (mapLoader.stations.contains(mouseCell)) {
               // mouseCell.
            //}
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
        mapRenderer.dispose();
        mapLoader.dispose();
        border.dispose();
    }
}
