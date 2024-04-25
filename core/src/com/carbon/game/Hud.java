package com.carbon.game;

import Screens.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import sun.jvm.hotspot.runtime.StaticBaseConstructor;

public class Hud {
    public Stage stage;
    private Image image;
    private SpriteBatch batch;
    private TextButton.TextButtonStyle textButtonStyle;
    private Skin skin;
    private BitmapFont black,white;
    private TextureAtlas atlas;
    private  Table table;
    private Viewport viewport;
    private OrthographicCamera camera;

    com.badlogic.gdx.scenes.scene2d.ui.Label countdownLabel;
    com.badlogic.gdx.scenes.scene2d.ui.Label scoreLabel;
    com.badlogic.gdx.scenes.scene2d.ui.Label carbonLabel;
    com.badlogic.gdx.scenes.scene2d.ui.Label energyLabel;

    public Hud(){
        batch = new SpriteBatch();
        white = new BitmapFont();
        white.setColor(256, 256, 256, 1);

        black = new BitmapFont();
        black.setColor(0, 0,0, 1);
        atlas = new TextureAtlas("ui/new_buttons.txt");
        skin = new Skin(atlas);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = black;
        textButtonStyle.up = skin.getDrawable("button_up");
        textButtonStyle.down = skin.getDrawable("button_down");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;

//        TextButton buttonCab = new TextButton("Pause", textButtonStyle);
//        buttonCab.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
//            }
//        });
//        buttonCab.pad(10);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        stage = new Stage(viewport,batch);

        table = new Table();
        image = new Image(new TextureRegion(new Texture(Gdx.files.internal("uiskin/hud2.png"))));
        image.setHeight(20);
        image.setWidth(Gdx.graphics.getWidth());
        image.setPosition(0,Gdx.graphics.getHeight()-image.getHeight());
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        countdownLabel = new Label("Time: "+String.format("%,.0f", (double) GameScreen.worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label("Score: "+String.format("%d",Player.score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        carbonLabel = new Label("Carbon: "+String.format("%d",Player.carbon), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        energyLabel = new Label("Energy: "+String.format("%03d",Player.energy), new Label.LabelStyle(new BitmapFont(), Color.WHITE));


//        table.add(countdownLabel).expandX().top();
//        table.add(scoreLabel).expand().top();
//        table.add(carbonLabel).expand().top();//carbon label
//        table.add(energyLabel).expand().top();
////        table.add(buttonCab).top().right();
//        stage.addActor(image);
//        stage.addActor(table);
    }
    public void update() {
        countdownLabel.setText("Time: "+String.format("%,.0f", (double) GameScreen.worldTimer));
        scoreLabel.setText("Score: "+String.format("%d",Player.score));
        carbonLabel.setText("Carbon: "+String.format("%d",Player.carbon));
        energyLabel.setText("Energy: "+String.format("%03d",Player.energy));

    }
    public void show() {
//        update();
        table.clear();
        table.add(countdownLabel).expandX().top();
        table.add(scoreLabel).expand().top();
        table.add(carbonLabel).expand().top();//carbon label
        table.add(energyLabel).expand().top();
//        table.add(buttonCab).top().right();
        stage.addActor(image);
        stage.addActor(table);
        stage.draw();
    }
    public void dispose() {
        stage.dispose();
        batch.dispose();
        black.dispose();
        white.dispose();
    }

}
