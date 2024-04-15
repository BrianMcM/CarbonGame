package com.carbon.game;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.xguzm.pathfinding.grid.GridCell;

public class GameScreen extends GridLogic implements Screen {
    private final CarbonGame game;
    private final OrthographicCamera camera;
    //class objects
    public Player player;
    //map
    public final OrthogonalTiledMapRenderer mapRenderer;
    public final OrthogonalTiledMapRenderer metroRenderer;
    public final Map mapLoader;
    //textures
    public final Texture border_img = new Texture(Gdx.files.internal("border.png"));
    public int[] building = null;
    public boolean metroVision = false;
    private boolean canClick = true;
    public ArrayList<Gem> gemList = new ArrayList<>();
    public final GemSpawner gemSpawner;
    private final Viewport viewport;

    //Use constructor instead of create here
    public GameScreen(final CarbonGame game) {
        this.game = game;
        player = new Player(this, 100, 5, 20);
        mapLoader = new Map(this, player);
        gemSpawner = new GemSpawner(mapLoader, this);

        float unitScale = 1f;
        mapRenderer = new OrthogonalTiledMapRenderer(mapLoader.map, unitScale);
        metroRenderer = new OrthogonalTiledMapRenderer(mapLoader.metro, unitScale);
        //camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        Vector3 inputPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(inputPos);

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
        int inputCellX = worldToCell(inputPos.x);
        int inputCellY = worldToCell(inputPos.y);
        GridCell inputCell = mapLoader.gridLayer.getCell(inputCellX, inputCellY);

        float borderX = cellToWorld(inputCellX) - (float) TILE_SIZE/2; //find cell of where mouse is pointing
        float borderY = cellToWorld(inputCellY) - (float) TILE_SIZE/2; // and return global position of the cell center

        game.batch.setProjectionMatrix(camera.combined);
        //sprite batch
        game.batch.begin();
        //player sprite
        if (!player.hide) {
            game.batch.draw(player.img, player.position.x, player.position.y, TILE_SIZE, TILE_SIZE);
        } else {
            if (building != null) {
                game.batch.setColor(Color.YELLOW);
                game.batch.draw(border_img, cellToWorld(building[0]) - (float) TILE_SIZE /2, cellToWorld(building[1]) - (float) TILE_SIZE /2, TILE_SIZE * 2, TILE_SIZE * 2);
                game.batch.setColor(Color.WHITE);
            }
        }
        //cell selector for mouse
        if (!player.move && !player.hide) {
            if (inputCell != null) {
                if (inputCell.isWalkable()) {
                    game.batch.draw(border_img, borderX, borderY, TILE_SIZE * 2, TILE_SIZE * 2);
                } else if (mapLoader.stationList.containsKey(inputCell)) {
                    game.batch.setColor(Color.YELLOW);
                    if (player.mode == 1) {
                        game.batch.draw(border_img, borderX, borderY, TILE_SIZE * 2, TILE_SIZE * 2);
                    } else if (player.mode == 2) {
                        if (mapLoader.bikeStands.containsKey(inputCell)) {
                            game.batch.draw(border_img, borderX, borderY, TILE_SIZE * 2, TILE_SIZE * 2);
                        }
                    }
                    game.batch.setColor(Color.WHITE);
                }
            }
        }
        //transit section
        for (Route route : mapLoader.routes) {
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
                    game.batch.draw(transit.img, transit.position.x, transit.position.y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        if (!metroVision) {
            //gems
            for (Gem gem : gemList){
                game.batch.draw(gem.img, gem.position.x, gem.position.y, TILE_SIZE, TILE_SIZE);
            }
            //cars
            for (Car car : mapLoader.cars) {
                if (!car.hidden) {
                    game.batch.draw(car.img, car.position.x, car.position.y, TILE_SIZE, TILE_SIZE);
                }
            }
        }
        game.batch.end();

        //click input movement
        if (Gdx.input.justTouched()) {
            //check that mouse isn't off-screen
            if (inputPos.x < 0 || inputPos.y < 0 || inputPos.x > viewport.getScreenWidth() || inputPos.y > viewport.getScreenHeight()) {
                return;
            }
            //if cell that player is in is clicked don't react
            if (inputCellX == player.cellX && inputCellY == player.cellY) {
                return;
            }
            //if cool down timer hasn't fired prevent click
            if (!canClick) {
                return;
            } else {
                canClick = false;
                clickCoolDown();
            }
            //exit building
            if (building != null) {
                player.exit();
                return;
            }
            //let player off the train
            if (player.transit != null) {
                player.transit.letPlayerOff = true;
                return;
            }
            //end trip at next cell, take no other inputs
            if (player.move) {
                player.finishEarly();
                return;
            }
            //if walkable start player movement
            if (mapLoader.gridLayer.getCell(inputCellX, inputCellY).isWalkable()) {
                player.setPath(mapLoader.path(player.cellX, player.cellY, inputCellX, inputCellY));
                return;
            }
            if (mapLoader.stationList.containsKey(inputCell)) {
                if (Objects.equals(mapLoader.stationList.get(inputCell), "bikeStations")) {
                    mapLoader.bikeStands.get(inputCell).select();
                } else {
                    mapLoader.stations.get(inputCell).select();
                }
            }
        }
        //player movement
        if (player.move) {
            float update = player.mode * 50 * Gdx.graphics.getDeltaTime() * player.exhausted;
            player.position.x -= player.norm.x * update;
            player.position.y -= player.norm.y * update;
            double buffer = player.mode * player.exhausted;
            if (Math.abs(player.position.x - player.target.x) < buffer && Math.abs(player.position.y - player.target.y) < buffer) {
                player.nextCell();
            }
        }

        //car movement
        for (Car car : mapLoader.cars) {
            if (car.move) {
                float carUpdate = 150 * Gdx.graphics.getDeltaTime();
                car.position.x -= car.norm.x * carUpdate;
                car.position.y -= car.norm.y * carUpdate;
                double carBuffer = 3;
                if (Math.abs(car.position.x - car.target.x) < carBuffer && Math.abs(car.position.y - car.target.y) < carBuffer) {
                    car.nextCell();
                }
            }
        }


        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            // Handle the 'A' key press event
            mapLoader.callCar();
            // Add your event handling code here
        }
    }

    private void clickCoolDown() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run () {
                allowClick();
            }
        }, (float) 0.2, 0, 0);
    }

    public void allowClick() {
        canClick = true;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        border_img.dispose();
        for (Gem gem : gemList) {
            gem.dispose();
        }
    }
}
