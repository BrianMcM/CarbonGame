package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

import java.util.ArrayList;
import java.util.Objects;

public class GameScreen extends GridLogic implements Screen {
    final private CarbonGame game;
    private final OrthographicCamera camera;
    //class objects
    public Player player;
    //map
    public OrthogonalTiledMapRenderer mapRenderer;
    public OrthogonalTiledMapRenderer metroRenderer;
    public Map mapLoader;
    //textures
    public Texture border = new Texture(Gdx.files.internal("border.png"));
    public int[] building = null;
    public boolean metroVision = false;
    private boolean canClick = true;
    public ArrayList<Gem> gemList = new ArrayList<>();
    public GemSpawner gemSpawner;
    private final Viewport viewport;
    public Music music_j = Gdx.audio.newMusic(Gdx.files.internal("SFX/Main_Music_City_Jazz.mp3"));
    public Music music_r = Gdx.audio.newMusic(Gdx.files.internal("SFX/Main_Music_Retro.mp3"));
    public Sound Game_start = Gdx.audio.newSound(Gdx.files.internal("SFX/win31.mp3"));
    public Sound Exit_Bus_Stop = Gdx.audio.newSound(Gdx.files.internal("SFX/Bus_door.wav"));
    public Sound Train_exit = Gdx.audio.newSound(Gdx.files.internal("SFX/metro_chime.wav"));

    //Use constructor instead of create here
    public GameScreen(final CarbonGame game) {
        this.game = game;
        //Sound at start of game
        //Game_start.play();
        player = new Player(this, 100, 5, 20);
        mapLoader = new Map(this, player);
        gemSpawner = new GemSpawner(mapLoader, this);
        //music_j = SimCity 3000 music, more smooth jazz, probably go
        // better with the more realistic sounds and vibe of the game
        music_j.setVolume(0.1f);
        music_j.play();
        //music_r = Same as before
        //music_r.setVolume(0.1f);
        //music_r.play();

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

        float borderX = cellToWorld(inputCellX) - (float) tileSize/2; //find cell of where mouse is pointing
        float borderY = cellToWorld(inputCellY) - (float) tileSize/2; // and return global position of the cell center

        game.batch.setProjectionMatrix(camera.combined);
        //sprite batch
        game.batch.begin();
        //player sprite
        if (!player.hide) {
            game.batch.draw(player.img, player.position.x, player.position.y, tileSize, tileSize);
        } else {
            if (building != null) {
                game.batch.setColor(Color.YELLOW);
                game.batch.draw(border, cellToWorld(building[0]) - (float) tileSize /2, cellToWorld(building[1]) - (float) tileSize /2, tileSize * 2, tileSize * 2);
                game.batch.setColor(Color.WHITE);
            }
        }
        //cell selector for mouse
        if (!player.move && !player.hide) {
            if (inputCell != null) {
                if (inputCell.isWalkable()) {
                    game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                } else if (mapLoader.stationList.containsKey(inputCell)) {
                    game.batch.setColor(Color.YELLOW);
                    if (player.mode == 1) {
                        game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                    } else if (player.mode == 2) {
                        if (mapLoader.bikeStands.containsKey(inputCell)) {
                            game.batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
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
                    game.batch.draw(transit.img, transit.position.x, transit.position.y, tileSize, tileSize);
                }
            }
        }
        if (!metroVision) {
            for (Gem gem : gemList){
                game.batch.draw(gem.img, gem.position.x, gem.position.y, tileSize, tileSize);
            }
        }
        game.batch.end();

        //click input movement
        if (Gdx.input.justTouched()) {
            if (!canClick) {
                return;
            } else {
                canClick = false;
                clickCoolDown();
            }
            if (building != null) {
                player.exit();
                //Exit_Bus_Stop.play();
                return;
            }
            if (player.transit != null) {
                player.transit.letPlayerOff = true;
                //Train_exit.play();
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

    private void allowClick() {
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
        border.dispose();
        music_r.dispose();
        music_j.dispose();
        Game_start.dispose();
        Exit_Bus_Stop.dispose();
        Train_exit.dispose();
        for (Gem gem : gemList) {
            gem.dispose();
        }
    }
}
