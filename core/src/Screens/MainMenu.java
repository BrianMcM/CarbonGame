package Screens;

import Screens.Tween.ActorAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.carbon.game.CarbonGame;

import java.sql.Time;

public class MainMenu implements Screen {
    private TweenManager tweenManager;
    private Stage stage;
    private Table table;//done
    private TextButton buttonPlay,buttonExit;
    private Label heading;
    private Skin skin;//done
    private BitmapFont white,black;//done
    private TextureAtlas atlas;//done
    private CarbonGame game;
    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        white = new BitmapFont();
        white.setColor(256, 256, 256, 1);

        black = new BitmapFont();
        black.setColor(0, 0,0, 1);

        atlas = new TextureAtlas("ui/inbetween.txt");
        skin = new Skin(atlas);

        table = new Table(skin);
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("start");
        textButtonStyle.down = skin.getDrawable("stop");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;

        buttonExit = new TextButton("Exit", textButtonStyle);
        buttonExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        buttonExit.pad(10);

        buttonPlay = new TextButton("Play", textButtonStyle);
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelsScreen());
//                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });
        Label.LabelStyle headingStyle = new Label.LabelStyle(white,Color.WHITE);

        heading = new Label("Carbon Game",headingStyle);
        heading.setFontScale(4);

        table.add(heading);
        table.row();
        table.padBottom(50);
        table.add(buttonPlay);
        table.getCell(buttonPlay).spaceBottom(10);
        table.row();
        table.add(buttonExit);

        table.debug();
        stage.addActor(table);

        //Creating animations
        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class,new ActorAccessor());

        //create animation
        Timeline.createSequence().beginSequence()
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(0,0,1))
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(0,1,0))
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(1,0,0))
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(1,1,0))
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(1,0,1))
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(0,1,1))
                .push(Tween.to(heading,ActorAccessor.RGB,.5f).target(1,1,1))
                .end().repeat(Tween.INFINITY,0).start(tweenManager);


        Timeline.createSequence().beginSequence()
                .push(Tween.set(buttonPlay,ActorAccessor.Alpha).target(0))
                .push(Tween.set(buttonExit,ActorAccessor.Alpha).target(0))
                .push(Tween.from(heading,ActorAccessor.Alpha,.5f).target(0))
                .push(Tween.to(buttonPlay,ActorAccessor.Alpha,.5f).target(1))
                .push(Tween.to(buttonExit,ActorAccessor.Alpha,.5f).target(1))
                .end().start(tweenManager);
        Tween.from(table,ActorAccessor.Alpha,.5f).target(0).start(tweenManager);
        Tween.from(table,ActorAccessor.Y,.5f).target(Gdx.graphics.getHeight()/8).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        table.setDebug(true);
        tweenManager.update(delta);
        stage.act(delta);

        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        atlas.dispose();
        skin.dispose();
        white.dispose();
        black.dispose();
    }
}
