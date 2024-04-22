package Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.carbon.game.GameScreen;
import com.carbon.game.Player;

public class ScoreScreen implements Screen {
    private Skin skin;
    private Stage stage;
    private Table table;
    private TextButton buttonMain,buttonReplay;
    private BitmapFont white,black;
    private Label heading,carbonStar,scoreStar,bonusStar;
    private Texture star,no_star;
    private Image star_carbon,star_no_carbon,star_energy,star_no_energy,star_gem,star_no_gem;
    @Override
    public void show() {
        star = new Texture(Gdx.files.internal("ui/star_full.png"));
        no_star = new Texture(Gdx.files.internal("ui/star_empty.png"));
        star_carbon = new Image(star);
        star_no_carbon = new Image(no_star);

        star_energy = new Image(star);
        star_no_energy = new Image(no_star);

        star_gem = new Image(star);
        star_no_gem = new Image(no_star);

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        white = new BitmapFont();
        white.setColor(256, 256, 256, 1);
        black = new BitmapFont();
        black.setColor(0, 0,0, 1);

        table = new Table(skin);
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        buttonMain = new TextButton("MainMenu",skin);
        buttonReplay = new TextButton("Replay",skin);

        buttonMain.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelsScreen());
//                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            }
        });
        buttonReplay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                ((Game) Gdx.app.getApplicationListener()).setScreen(new LevelsScreen());
                ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen("testMap/map_final.tmx","testMap/metro_final.tmx", 300));
            }
        });

        Label.LabelStyle headingStyle = new Label.LabelStyle(white, Color.WHITE);

        heading = new Label("Score",headingStyle);
        carbonStar = new Label("Carbon Score "+ String.format("%03d",Player.carbon),headingStyle);

        scoreStar = new Label("Gem Score "+String.format("%04d", Player.score),headingStyle);
        bonusStar = new Label("Bonus Star "+String.format("%03d",Player.energy),headingStyle);
        heading.setFontScale(4);

        table.add();
        table.add(heading);
        table.row();
        table.add(carbonStar);
        table.add(scoreStar);
        table.add(bonusStar);
        table.row();
        if(Player.carbon>100){table.add(star_no_carbon);}else{table.add(star_carbon);}
        if(Player.score<300){table.add(star_no_gem);}else{table.add(star_gem);}
        if(Player.energy<50){table.add(star_no_energy);}else{table.add(star_energy);}

        table.row();
        table.padBottom(50);
        table.add();
        table.add(buttonMain);
        table.getCell(buttonMain).spaceBottom(10);
        table.row();
        table.add();
        table.add(buttonReplay);

        table.debug();
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        table.setDebug(true);
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

        skin.dispose();

        white.dispose();
        black.dispose();

    }
}
