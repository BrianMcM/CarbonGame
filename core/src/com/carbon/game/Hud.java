package com.carbon.game;

import Screens.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud {
    public Stage stage;

    com.badlogic.gdx.scenes.scene2d.ui.Label countdownLabel;
    com.badlogic.gdx.scenes.scene2d.ui.Label scoreLabel;
    com.badlogic.gdx.scenes.scene2d.ui.Label carbonLabel;
    com.badlogic.gdx.scenes.scene2d.ui.Label energyLabel;
    public Hud(SpriteBatch sb){
        BitmapFont white = new BitmapFont();
        white.setColor(256, 256, 256, 1);

        BitmapFont black = new BitmapFont();
        black.setColor(0, 0,0, 1);
        TextureAtlas atlas = new TextureAtlas("ui/inbetween.txt");
        Skin skin = new Skin(atlas);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = black;
        textButtonStyle.up = skin.getDrawable("start");
        textButtonStyle.down = skin.getDrawable("stop");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;

        TextButton buttonCab = new TextButton("Call Cab", textButtonStyle);
        buttonCab.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenu());
            }
        });
        buttonCab.pad(10);

        OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Viewport viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        stage = new Stage(viewport,sb);
        Table table = new Table();
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        countdownLabel = new Label(String.format("%,.0f", (double) GameScreen.worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d",Player.score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        carbonLabel = new Label(String.format("%03d",Player.carbon), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        energyLabel = new Label(String.format("%06d",Player.energy), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(countdownLabel).expandX().top();
        table.add(scoreLabel).expand().top();
        table.add(carbonLabel).expand().top();//carbon label
        table.add(energyLabel).expand().top();
        table.add(buttonCab).top().right();
        stage.addActor(table);

    }


}
