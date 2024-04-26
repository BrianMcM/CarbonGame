package com.carbon.game;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Objects;

import Screens.DialogPopup;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.xguzm.pathfinding.grid.GridCell;
import static java.lang.System.*;


public class GameScreen extends GridLogic implements Screen {
    private final CarbonGame game;
    private final OrthographicCamera camera;
    //class objects
    public Player player;
    //map
    public PopupText popupText = new PopupText();
    public final OrthogonalTiledMapRenderer mapRenderer;
    public final OrthogonalTiledMapRenderer metroRenderer;
    public final Map mapLoader;
    //textures
    public final Texture border_img = new Texture(Gdx.files.internal("border.png"));
    public Station stationInside = null;
    public boolean metroVision = false;
    private boolean canClick = true;
    public ArrayList<Gem> gemList = new ArrayList<>();
    public GemSpawner gemSpawner;
    private final Viewport viewport;
    private final Hud hud;

    public SpriteBatch batch;
    public BitmapFont font;
    public static Float worldTimer = (float) 10;
    private final Stage stage;
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_CARBON_POP = 3;
    static final int GAME_CARBON_POP_2 = 4;
    static final int GAME_SCORE_POP = 5;
    static final int GAME_ENERGY_POP = 6;
    static final int GAME_TIME_POP = 7;
    static final int GAME_GEM_POP = 8;
    static final int GAME_INITIAL_POP = 9;
    int state = 1;
    private Boolean popuped = true;
    private Boolean popuped_carbon = true;
    private Boolean popuped_carbon_2 = true;
    private Boolean popuped_time = true;
    private Boolean popuped_energy = true;
    private Boolean popuped_gems = true;


    public Music music_j = Gdx.audio.newMusic(Gdx.files.internal("SFX/Main_Music_City_Jazz.mp3"));
    public Sound Train_exit = Gdx.audio.newSound(Gdx.files.internal("SFX/metro_chime.wav"));
    public Music music_end = Gdx.audio.newMusic(Gdx.files.internal("SFX/10_Second_Track.mp3"));
    public Sound Popup = Gdx.audio.newSound(Gdx.files.internal("SFX/popup.mp3"));

    public GameScreen(CarbonGame game, String mapName, String metroName, float time, String levelroutes) {
        this.game = game;
        stage = new Stage();
        batch = new SpriteBatch();
        font = new BitmapFont();
        player = new Player(this, 100, 5, 20);
        music_j.setVolume(0.2f);
        music_j.play();

        //Added the names of the map files here so different maps could be passed in the future
        mapLoader = new Map(this, player,mapName,metroName, levelroutes);
        gemSpawner = new GemSpawner(mapLoader, this, 5);

        float unitScale = 1f;
        mapRenderer = new OrthogonalTiledMapRenderer(mapLoader.map, unitScale);
        metroRenderer = new OrthogonalTiledMapRenderer(mapLoader.metro, unitScale);
        //camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        hud = new Hud();
        worldTimer = time;
    }
    public static void timer_world(Float t){
        worldTimer += t;
    }
    @Override
    public void render(float delta) {

//        hud.dispose();
        if (state == GAME_RUNNING) {
            timer_world(-delta);//update timer for world

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

            float borderX = cellToWorld(inputCellX) - (float) TILE_SIZE / 2; //find cell of where mouse is pointing
            float borderY = cellToWorld(inputCellY) - (float) TILE_SIZE / 2; // and return global position of the cell center

            batch.setProjectionMatrix(camera.combined);
            //sprite batch
            batch.begin();
            //player sprite
            if (!player.hide) {
                if (player.xFlip) {
                    batch.draw(player.img, player.position.x - 8, player.position.y - 8, TILE_SIZE * 2, TILE_SIZE * 2);
                } else {
                    batch.draw(player.img, player.position.x + 24, player.position.y - 8, TILE_SIZE * -2, TILE_SIZE * 2);
                }

            } else {
                if (stationInside != null) {
                    batch.setColor(Color.YELLOW);
                    batch.draw(border_img, cellToWorld(stationInside.cell.getX()) - (float) TILE_SIZE / 2, cellToWorld(stationInside.cell.getY()) - (float) TILE_SIZE / 2, TILE_SIZE * 2, TILE_SIZE * 2);
                    batch.setColor(Color.WHITE);
                }
            }
            //cell selector for mouse
            if (!player.move && !player.hide) {
                if (inputCell != null) {
                    if (inputCell.isWalkable()) {
                        batch.draw(border_img, borderX, borderY, TILE_SIZE * 2, TILE_SIZE * 2);
                    } else if (mapLoader.stationList.containsKey(inputCell)) {
                        batch.setColor(Color.YELLOW);
                        if (player.mode == 1) {
                            batch.draw(border_img, borderX, borderY, TILE_SIZE * 2, TILE_SIZE * 2);
                        } else if (player.mode == 2) {
                            if (mapLoader.bikeStands.containsKey(inputCell)) {
                                batch.draw(border_img, borderX, borderY, TILE_SIZE * 2, TILE_SIZE * 2);
                            }
                        }
                        batch.setColor(Color.WHITE);
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
                        batch.draw(transit.img, transit.position.x, transit.position.y, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
            if (!metroVision) {
                //gems
                for (Gem gem : gemList) {
                    batch.draw(gem.img, gem.position.x - 8, gem.position.y - 8, TILE_SIZE * 2, TILE_SIZE * 2);
                }
                //cars
                for (Car car : mapLoader.cars) {
                    if (!car.hidden) {
                        batch.draw(car.img, car.position.x, car.position.y, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
            batch.end();
            //click input movement
            if (Gdx.input.justTouched()) {
                if (!canClick) {
                    return;
                } else {
                    canClick = false;
                    clickCoolDown();
                }
                //exit building
                if (stationInside != null) {
                    stationInside.playerExit();
                    return;
                }
                //let player off the train
                if (player.transit != null) {
                    player.transit.letPlayerOff = true;
                    return;
                }
              //cant move once cars coming
                if (player.carCalled) {
                    return;
                }
                //end trip at next cell, take no other inputs
                if (player.move) {
                    player.finishEarly();
                    return;
                }
                if (player.mode == 3) {
                    for (int[] coord : mapLoader.drivableTiles) {
                        if (coord[0] == inputCellX && coord[1] == inputCellY) {
                            player.setPath(mapLoader.carPath(player.cellX, player.cellY, inputCellX, inputCellY));
                        }
                    }
                } else {
                    //if walkable start player movement
                    if (mapLoader.gridLayer.getCell(inputCellX, inputCellY).isWalkable()) {
                        player.setPath(mapLoader.path(player.cellX, player.cellY, inputCellX, inputCellY));
                        return;
                    }
                    if (mapLoader.stationList.containsKey(inputCell)) {
                        if (Objects.equals(mapLoader.stationList.get(inputCell), "bikeStation")) {
                            mapLoader.bikeStands.get(inputCell).select();
                        } else {
                            mapLoader.stations.get(inputCell).select();
                        }
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
                    double carBuffer = 4;
                    if (Math.abs(car.position.x - car.target.x) < carBuffer && Math.abs(car.position.y - car.target.y) < carBuffer) {
                        car.nextCell();
                    }
                }
            }
        } else {
            Dialog popup = new Dialog("Hint", DialogPopup.skin) {
                {
                    String[] string = getString();
                    text(string[0]);
                    button("Continue", false);
                    if (state == GAME_PAUSED) {
                        button("Main Menu", true);
                    }
                    setWidth(Float.parseFloat(string[1]));
                    setHeight(Float.parseFloat(string[2]));
                }

                private String[] getString() {
                    String string = null;
                    int widthBox = 0;
                    int heightBox = 0;

                    switch (state) {
                        case GAME_PAUSED:
                            string = popupText.gamePaused;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                        case GAME_CARBON_POP:
                            string = popupText.carbonPopup;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                        case GAME_CARBON_POP_2:
                            string = popupText.carbonPopup_2;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                        case GAME_GEM_POP:
                            string = popupText.gemPopup;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                        case GAME_ENERGY_POP:
                            string = popupText.energyPopup;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                        case GAME_TIME_POP:
                            string = popupText.timePopup;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                        case GAME_INITIAL_POP:
                            string = popupText.gameInitialised;
                            widthBox = 700;
                            heightBox = 400;
                            break;
                    }

                    return new String[]{string, String.valueOf(widthBox), String.valueOf(heightBox)};
                }

                @Override
                public Dialog show(Stage stage) {
                    return super.show(stage);
                }


                @Override
                protected void result(Object object) {
                    if ((Boolean) object) {
                        game.pickScreen(1);
                        Gdx.app.exit();
                    } else {
                        state = GAME_RUNNING;
                    }
                }
            };

            popup.setPosition(Math.round((stage.getWidth() - popup.getWidth()) / 2), Math.round((stage.getHeight() - popup.getHeight()) / 2));
            stage.addActor(popup);
            stage.draw();
            Gdx.input.setInputProcessor(stage);
        }


        hud.update();
        hud.show();
      
        //PAUSING
        if (worldTimer < -5 && popuped) {
            popuped = false;
            state = GAME_PAUSED;
            out.println("popuped");
            out.println(popupText.carbonPopup);
        }
        if (worldTimer <(float) 199.00 && popuped) {
            popuped = false;
            state = GAME_INITIAL_POP;
            Popup.play();
        }
        if (worldTimer <(float) 100.00 && popuped_time) {
            popuped_time = false;
            state = GAME_TIME_POP;
            Popup.play();
        }
        if (Player.carbon > 10 && popuped_carbon) {
            popuped_carbon = false;
            state = GAME_CARBON_POP;
            out.println("popup_carbon");
            Popup.play();
        }
        if (Player.carbon > 2000 && popuped_carbon_2) {
            popuped_carbon_2 = false;
            state = GAME_CARBON_POP_2;
            Popup.play();
        }

        if (Player.score >= 100 && popuped_gems) {
            popuped_gems = false;
            state = GAME_GEM_POP;
            out.println("popup_gem");
            Popup.play();
        }
        if (Player.energy < 30 && popuped_energy) {
            popuped_energy = false;
            state = GAME_ENERGY_POP;
            out.println("popup_energy");
            Popup.play();
        }
        if (worldTimer <= 50) {
            music_j.stop();
            music_end.setVolume(0.2f);
            music_end.play();
        }
        if (worldTimer < 0) {
            worldTimer = (float) 200.00;
            game.pickScreen(3);
            music_end.stop();
        }
        if (Player.carbon > 3000) {
            worldTimer = (float) 200.00;
            game.pickScreen(3);
            music_end.stop();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            mapLoader.callCar();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            state = GAME_PAUSED;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            for (Route r : mapLoader.routes) {
                for (Transit t : r.transitList) {
                    t.arriveAtTarget();
                }
            }
        }
//dispose();
    }
    //end of render loop
        private void clickCoolDown() {
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    allowClick();
                }
            }, (float) 0.2, 0, 0);
        }

        public void allowClick () {
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
        dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        hud.dispose();
        batch.dispose();
        font.dispose();
        player.dispose();
        mapRenderer.dispose();
        metroRenderer.dispose();
        mapLoader.dispose();
        music_end.dispose();
        music_j.dispose();
        Popup.dispose();
        Train_exit.dispose();
        border_img.dispose();
        for (Gem gem : gemList) {
            gem.dispose();
        }
    }
}
