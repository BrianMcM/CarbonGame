package com.carbon.game;

import java.lang.Math;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import org.xguzm.pathfinding.grid.GridCell;

public class GameScreen extends GridLogic implements Screen {
    final private CarbonGame game;
    public OrthographicCamera camera;
    //class objects
    public Player player;
    //map
    public OrthogonalTiledMapRenderer mapRenderer;
    public OrthogonalTiledMapRenderer metroRenderer;
    public Map mapLoader;
    //textures
    public Texture border = new Texture(Gdx.files.internal("border.png"));
    public int[] inUseTile = null;
    public boolean metroVision = false;

    //Use constructor instead of create here
    public GameScreen(final CarbonGame game) {
        this.game = game;
        player = new Player(this, 100, 5, 20);
        mapLoader = new Map(this, player);

        float unitScale = 1f;
        mapRenderer = new OrthogonalTiledMapRenderer(mapLoader.map, unitScale);
        metroRenderer = new OrthogonalTiledMapRenderer(mapLoader.metro, unitScale);
        //camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        //render tile map
        if (metroVision) {
            metroRenderer.render();
            camera.update();
            metroRenderer.setView(camera);
        } else {
            mapRenderer.render();
            camera.update();
            mapRenderer.setView(camera);
        }
        //track mouse movement for cell border
        int mouseCellX = worldToCell(Gdx.input.getX());
        int mouseCellY = worldToCell(Gdx.input.getY());
        GridCell mouseCell = mapLoader.gridLayer.getCell(mouseCellX, 56 - mouseCellY);

        int tileSize = 16;
        float borderX = cellToWorld(mouseCellX) - (float) tileSize /2; //find cell of where mouse is pointing
        //no idea why 56 works here
        float borderY = cellToWorld(56 - mouseCellY) - (float) tileSize /2; // and return global position of the cell center

        game.batch.setProjectionMatrix(camera.combined);
        //sprite batch
        game.batch.begin();
        //player sprite
        if (!player.hide) {
            game.batch.draw(player.img, player.position.x, player.position.y, tileSize, tileSize);
        } else {
            if (inUseTile != null) {
                game.batch.setColor(Color.YELLOW);
                game.batch.draw(border, cellToWorld(inUseTile[0]) - (float) tileSize /2, cellToWorld(inUseTile[1]) - (float) tileSize /2, tileSize * 2, tileSize * 2);
                game.batch.setColor(Color.WHITE);
            }
        }
        //cell selector for mouse
        if (!player.move && !player.hide) {
            if (mouseCell != null) {
                if (mouseCell.isWalkable()) {
                    game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                } else if (mapLoader.stationList.containsKey(mouseCell)) {
                    game.batch.setColor(Color.YELLOW);
                    if (player.mode == 1) {
                        game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                    } else if (player.mode == 2) {
                        if (mapLoader.bikeStands.containsKey(mouseCell)) {
                            game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                        }
                    }
                    game.batch.setColor(Color.WHITE);
                }
            }
        }
        //transit section
        for (Route route : mapLoader.Routes) {
            for (Transit transit : route.transitList) {
                if (transit.move) {
                    float update = transit.speed * Gdx.graphics.getDeltaTime();
                    transit.position.x -= transit.norm.x * update;
                    transit.position.y -= transit.norm.y * update;
                    if (Math.abs(transit.position.x - transit.target.x) < transit.buffer && Math.abs(transit.position.y - transit.target.y) < transit.buffer) {
                        transit.arriveAtTarget();
                    }
                }
                if ((metroVision && route.train) || (!metroVision && !route.train)) {
                    game.batch.draw(transit.img, transit.position.x, transit.position.y, tileSize, tileSize);
                }
            }
        }
        game.batch.end();

        //click input movement
        if (Gdx.input.justTouched()) {
            if (inUseTile != null) {
                player.exit();
                return;
            }
            if (player.transit != null) {
                player.transit.letPlayerOff = true;
                return;
            }
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
            if (mapLoader.gridLayer.getCell(touchedCellX, touchedCellY).isWalkable()) {
                player.setPath(mapLoader.path(player.cellX, player.cellY, touchedCellX, touchedCellY));
                return;
            }
            if (mapLoader.stationList.containsKey(mouseCell)) {
                if (Objects.equals(mapLoader.stationList.get(mouseCell), "bikeStations")) {
                    mapLoader.bikeStands.get(mouseCell).select();
                } else {
                    mapLoader.Stations.get(mouseCell).select();
                }
            }
        }
        //player movement
        if (player.move) {
            float update = player.mode * 50 * Gdx.graphics.getDeltaTime();
            player.position.x -= player.norm.x * update;
            player.position.y -= player.norm.y * update;
            if (Math.abs(player.position.x - player.target.x) < player.mode && Math.abs(player.position.y - player.target.y) < player.mode) {
                player.nextCell();
            }
        }
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
        metroRenderer.dispose();
        mapLoader.dispose();
        border.dispose();
    }
}
