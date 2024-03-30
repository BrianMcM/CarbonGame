package com.carbon.game;

import java.lang.Math;

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
        //metro vision
        if (metroVision) {
            metroRenderer.render();
            camera.update();
            metroRenderer.setView(camera);
            game.batch.setProjectionMatrix(camera.combined);
            //sprite batch
            game.batch.begin();
            for (TrainLine line : mapLoader.trainLines) {
                Train train = line.train;
                if (train.move) {
                    float update = train.speed * Gdx.graphics.getDeltaTime();
                    train.position.x -= train.norm.x * update;
                    train.position.y -= train.norm.y * update;
                    if (Math.abs(train.position.x - train.target.x) < train.buffer && Math.abs(train.position.y - train.target.y) < train.buffer) {
                        train.arriveAtTarget();
                    }
                }
                game.batch.draw(train.img, train.position.x, train.position.y, tileSize, tileSize);
            }
            if (inUseTile != null) {
                game.batch.setColor(Color.YELLOW);
                game.batch.draw(border, cellToWorld(inUseTile[0]) - (float) tileSize /2, cellToWorld(inUseTile[1]) - (float) tileSize /2, tileSize * 2, tileSize * 2);
                game.batch.setColor(Color.WHITE);
            }
            game.batch.end();

            if (Gdx.input.justTouched()) {
                player.getOffTrain();
            }
            return;
        }

        //render tile map
        mapRenderer.render();
        camera.update();
        mapRenderer.setView(camera);

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
        }
        //cell selector for mouse
        if (!player.move) {
            if (mouseCell != null) {
                if (mouseCell.isWalkable()) {
                    game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                } else if (mapLoader.trainStations.containsKey(mouseCell)) {
                    game.batch.setColor(Color.YELLOW);
                    game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                    game.batch.setColor(Color.WHITE);
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
            if (mapLoader.gridLayer.getCell(touchedCellX, touchedCellY).isWalkable()) {
                player.setPath(mapLoader.path(player.cellX, player.cellY, touchedCellX, touchedCellY));
                return;
            }
            if (mapLoader.trainStations.containsKey(mouseCell)) {
                mapLoader.trainStations.get(mouseCell).select();
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
