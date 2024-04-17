package Screens;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;


import com.badlogic.gdx.utils.viewport.Viewport;
import com.carbon.game.*;
import org.xguzm.pathfinding.grid.GridCell;
import sun.tools.jconsole.JConsole;

import static java.lang.System.*;


public class GameScreen extends GridLogic implements Screen {
    //final private CarbonGame game;
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
    private Viewport viewport;
    private Hud hud;

    public static final int WORLD_WIDTH = 1280;
    public static final int WORLD_HEIGHT = 800;
    public float aspectRatio;

    public SpriteBatch batch;
    public BitmapFont font;
    public static Float worldTimer = (float) 300.00;
    private Stage stage;
    private DialogPopup popup;
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;
    int state = 1;
    private Boolean popuped = true;




    //Use constructor instead of create here
    public GameScreen() {
        stage = new Stage();
        batch = new SpriteBatch();
        font = new BitmapFont();
        /* this.game = game; */
        player = new Player(this, 100, 5, 20);

        //Added the names of the map files here so different maps could be passed in the future
        mapLoader = new Map(this, player,"testMap/new.tmx","testMap/metro.tmx");
        gemSpawner = new GemSpawner(mapLoader, this);

        aspectRatio = (float) WORLD_WIDTH / WORLD_HEIGHT;
        //float unitScale = 1f;
        mapRenderer = new OrthogonalTiledMapRenderer(mapLoader.map);
        metroRenderer = new OrthogonalTiledMapRenderer(mapLoader.metro);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        hud = new Hud(batch);
    }
    public static void timer_world(Float t){
        worldTimer +=t;
    }
    @Override
    public void render(float delta) {
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

            float borderX = cellToWorld(inputCellX) - (float) tileSize / 2; //find cell of where mouse is pointing
            float borderY = cellToWorld(inputCellY) - (float) tileSize / 2; // and return global position of the cell center

            batch.setProjectionMatrix(camera.combined);
            batch.setProjectionMatrix(hud.stage.getCamera().combined);
            hud = new Hud(batch);


            //sprite batch
            batch.begin();
            //player sprite
            if (!player.hide) {
                batch.draw(player.img, player.position.x, player.position.y, tileSize, tileSize);
            } else {
                if (building != null) {
                    batch.setColor(Color.YELLOW);
                    batch.draw(border, cellToWorld(building[0]) - (float) tileSize / 2, cellToWorld(building[1]) - (float) tileSize / 2, tileSize * 2, tileSize * 2);
                    batch.setColor(Color.WHITE);
                }
            }
            //cell selector for mouse
            if (!player.move && !player.hide) {
                if (inputCell != null) {
                    if (inputCell.isWalkable()) {
                        batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                    } else if (mapLoader.stationList.containsKey(inputCell)) {
                        batch.setColor(Color.YELLOW);
                        if (player.mode == 1) {
                            batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
                        } else if (player.mode == 2) {
                            if (mapLoader.bikeStands.containsKey(inputCell)) {
                                batch.draw(border, borderX, borderY, tileSize * 2, tileSize * 2);
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
                        batch.draw(transit.img, transit.position.x, transit.position.y, tileSize, tileSize);
                    }
                }
            }
            if (!metroVision) {
                for (Gem gem : gemList) {
                    batch.draw(gem.img, gem.position.x, gem.position.y, tileSize, tileSize);
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
                if (building != null) {
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

        }else {
            Dialog popup = new Dialog("Popup", DialogPopup.skin){
                {
                    text("Are you sure you want to exit?");
                    button("Yes",true);
                    button("No",false);

                }

                @Override
                public Dialog show(Stage stage) {
                    return super.show(stage);
                }

                @Override
                protected void result(Object object) {
                    if((boolean) object) {
                        Gdx.app.exit();
                    }else{
                        state = GAME_RUNNING;
                    }
                }
            };


//
//            popup.show(stage);
            popup.setWidth(300);
            popup.setHeight(300);
            popup.setPosition(Math.round((stage.getWidth() - popup.getWidth()) / 2), Math.round((stage.getHeight() - popup.getHeight()) / 2));
            stage.addActor(popup);
            stage.draw();
            Gdx.input.setInputProcessor(stage);
        }
        hud.stage.draw();

        if(worldTimer<298 && popuped){
            popuped = false;
            state = GAME_PAUSED;
            out.println("popuped");
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
        batch.dispose();
        font.dispose();
        player.dispose();
        mapRenderer.dispose();
        metroRenderer.dispose();
        mapLoader.dispose();
        border.dispose();
        for (Gem gem : gemList) {
            gem.dispose();
        }
    }

}
