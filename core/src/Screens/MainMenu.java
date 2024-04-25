package Screens;

import Screens.Tween.ActorAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.carbon.game.CarbonGame;

public class MainMenu implements Screen {
    private TweenManager tweenManager;
    private Stage stage;
    private Table table;
    private Skin skin;
    private BitmapFont white,black;
    private TextureAtlas atlas;
    private final CarbonGame game;
    private Skin skinny;

    public MainMenu(CarbonGame g) {
        game = g;
    }

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        white = new BitmapFont(Gdx.files.internal("fonts/b.fnt"),false);
        white.setColor(256, 256, 256, 1);

        black = new BitmapFont(Gdx.files.internal("fonts/earthair.fnt"),false);
        black.setColor(0, 0,0, 1);

        atlas = new TextureAtlas("ui/new_buttons.txt");
        skin = new Skin(atlas);

        table = new Table(skin);
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("button_up");
        textButtonStyle.down = skin.getDrawable("button_down");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;
        textButtonStyle.fontColor = new Color(84f/256f,116f/256f,20f/256f, 1);

        TextButton buttonExit = new TextButton("Exit", textButtonStyle);
        TextureAtlas atlas1 = new TextureAtlas(Gdx.files.internal("uiskin/uiskin.atlas"));
        skinny = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        buttonExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new Dialog("confirm exit",skinny){
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
                        }
                    }
                };
                dialog.show(stage);

            }
        });


        TextButton buttonPlay = new TextButton("Play", textButtonStyle);
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pickScreen(2);
            }
        });
        Label.LabelStyle headingStyle = new Label.LabelStyle(white,Color.WHITE);

        Label heading = new Label("Carbon World", headingStyle);
        heading.setFontScale((float)0.7);
        buttonPlay.pad(15);
        buttonExit.pad(15);
        table.background(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("uiskin/background_menu.png")))));
        //row 1
        table.add(heading).colspan(7).spaceBottom(200);
        table.row();
        //row2
        table.add();
        table.add();
        table.add(buttonPlay);
        table.add();
        table.add(buttonExit);
        table.add();
        table.add();

//        table.debug();
        stage.addActor(table);

        //Creating animations
        tweenManager = new TweenManager();
        Tween.registerAccessor(Actor.class,new ActorAccessor());

        //create animation
        Timeline.createSequence().beginSequence()
                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(91f/256f,75f/256f,58f/256f))
                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(84f/256f,116f/256f,20f/256f))
                .end().start(tweenManager);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        table.setDebug(true);
        tweenManager.update(delta);
        stage.act(delta);
        table.background(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("uiskin/background_menu.png")))));
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
        skinny.dispose();
    }
}
