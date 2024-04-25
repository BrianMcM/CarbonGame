package Screens;

import Screens.Tween.ActorAccessor;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.carbon.game.CarbonGame;

public class LevelsScreen implements Screen {
    private TweenManager tweenManager;
    private Stage stage;
    private Skin skin;
    private BitmapFont white,black;
    private TextureAtlas atlas;
    private final CarbonGame game;
    private List.ListStyle listStyle_;
    private List list,list_;
    private java.lang.String[] highScore;
    private ScrollPane scrollPane_;
    public Sound Button = Gdx.audio.newSound(Gdx.files.internal("SFX/button.mp3"));

    public LevelsScreen(CarbonGame g) {
        game = g;
    }
    @Override
    public void show() {
        black = new BitmapFont(Gdx.files.internal("fonts/earthair.fnt"),false);
        int x = 54;
        Texture texture = new Texture(Gdx.files.internal("ui/button_down.png"));
        List.ListStyle listStyle = new List.ListStyle(black, new Color(84f / 256f, 116f / 256f, 20f / 256f, 1), new Color(91f / 256f, 75f / 256f, 58f / 256f, 1), new TextureRegionDrawable(new TextureRegion(texture)));
        list = new List(listStyle);
        String[] stringy = new String[]{"      Tutorial", "      Level ONE"};

        list.setItems(stringy);


        ScrollPane scrollPane = new ScrollPane(list);


        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        white = new BitmapFont(Gdx.files.internal("fonts/a.fnt"),false);
        white.setColor(256, 256, 256, 1);

        atlas = new TextureAtlas("ui/new_buttons.txt");
        skin = new Skin(atlas);

        Table table = new Table(skin);
        Table buttonTable = new Table(skin);
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        table.background(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("uiskin/screen_level_background.png")))));
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("button_up");
        textButtonStyle.down = skin.getDrawable("button_down");
        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;
        textButtonStyle.fontColor = new Color(84f/256f,116f/256f,20f/256f, 1);
        TextButton buttonExit = new TextButton("Back", textButtonStyle);
        buttonExit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Button.play();
                game.pickScreen(1);
            }
        });

        TextButton buttonHighScore = new TextButton("HighScore", textButtonStyle);
        buttonHighScore.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Button.play();
                Dialog popup = new Dialog("HighScores", DialogPopup.skin) {
                    {
                        Json json = new Json();
//                        String text = json.toJson(, Object.class);
                        JsonValue root = new JsonReader().parse(Gdx.files.internal("Scores/scores.json"));
//                        JsonValue base = json.parse();

                        text(root.toString());
                        button("Continue", false);
                    }
                    @Override
                    public Dialog show(Stage stage) {
                        return super.show(stage);
                    }
                };
                popup.show(stage);
            }
        });


        TextButton buttonPlay = new TextButton("Play", textButtonStyle);
        buttonPlay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (list.getSelected() == "      Level ONE") {
                    Button.play();
                    game.pickScreen(5);
                } else if (list.getSelected() == "      Tutorial") {
                    Button.play();
                    game.pickScreen(4);
                }
            }
        });
        Label.LabelStyle headingStyle = new Label.LabelStyle(white,Color.WHITE);

        Label heading = new Label("Select Level", headingStyle);
        heading.setFontScale((float)0.7);

        buttonPlay.pad(10);
        buttonExit.pad(10);
        buttonHighScore.pad(10);
//        Adding Things to Table
        table.add().width(table.getWidth()/3);
        table.add(heading).width(table.getWidth()/3).center();
        table.add().width(table.getWidth()/3).row();
        table.add(scrollPane).size(600, 600).expandY().colspan(2).right().spaceRight(50);
        table.padBottom(50);
//        table.add(buttonPlay);
        buttonTable.add(buttonPlay).spaceBottom(10);
        buttonTable.row();
        buttonTable.add(buttonExit).spaceBottom(10);
        buttonTable.row();
        buttonTable.add(buttonHighScore).spaceBottom(10);
        table.add(buttonTable).left();
//        table.getCell(buttonPlay).spaceBottom(10);
//        table.row();
//        table.add();
//        table.add();
//        table.add(buttonExit).spaceBottom(10);

//        table.debug();
        stage.addActor(table);

        //Creating animations
        tweenManager = new TweenManager();

        Tween.registerAccessor(Actor.class,new ActorAccessor());

        //create animation
        Timeline.createSequence().beginSequence()
                .push(Tween.to(heading,ActorAccessor.RGB,.0001f).target(91f/256f,75f/256f,58f/256f))
//                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(91f/256f,75f/256f,58f/256f))
//                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(199f/256f,147f/256f,104f/256f))
//                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(241f/256f,224f/256f,203f/256f))
//                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(223f/256f,181f/256f,133f/256f))
//                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(150f/256f,107f/256f,78f/256f))
                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(84f/256f,116f/256f,20f/256f))
                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(91f/256f,75f/256f,58f/256f))
//                .push(Tween.to(heading,ActorAccessor.RGB,3f).target(118f/256f,110f/256f,95f/256f))
                .end().repeat(Tween.INFINITY,0).start(tweenManager);


//        Timeline.createSequence().beginSequence()
//                .push(Tween.set(buttonPlay,ActorAccessor.Alpha).target(0))
//                .push(Tween.set(buttonExit,ActorAccessor.Alpha).target(0))
//                .push(Tween.from(heading,ActorAccessor.Alpha,.5f).target(0))
//                .push(Tween.to(buttonPlay,ActorAccessor.Alpha,.5f).target(1))
//                .push(Tween.to(buttonExit,ActorAccessor.Alpha,.5f).target(1))
//                .end().start(tweenManager);
//        Tween.from(table,ActorAccessor.Alpha,.5f).target(0).start(tweenManager);
//        Tween.from(table,ActorAccessor.Y,.5f).target((float) Gdx.graphics.getHeight() /8).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        table.setDebug(true);
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
        Button.dispose();
    }
}
