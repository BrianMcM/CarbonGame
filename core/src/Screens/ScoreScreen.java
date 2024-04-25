package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.carbon.game.CarbonGame;
import com.carbon.game.Player;

public class ScoreScreen implements Screen {
    private final CarbonGame game;
    private Skin skin;
    private Stage stage;
    private BitmapFont white,black;

    public ScoreScreen(CarbonGame g) {
        game = g;
    }
    @Override
    public void show() {
        black = new BitmapFont(Gdx.files.internal("fonts/a.fnt"),false);
        Texture star = new Texture(Gdx.files.internal("uiskin/red_checkmark.png"));
        Texture no_star = new Texture(Gdx.files.internal("uiskin/red_cross.png"));

        Image star_carbon = new Image(star);
        Image star_no_carbon = new Image(no_star);


        Image star_energy = new Image(star);
        Image star_no_energy = new Image(no_star);

        Image star_gem = new Image(star);
        Image star_no_gem = new Image(no_star);

        stage = new Stage();
        skin = new Skin(Gdx.files.internal("uiskin/uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        white = new BitmapFont();


        Table table = new Table(skin);
        table.background(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("uiskin/screen_level_background.png")))));

        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());


        TextButton buttonMain = new TextButton("Level Selector", skin);
        TextButton buttonReplay = new TextButton("Replay", skin);

        buttonMain.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pickScreen(2);
            }
        });
        buttonReplay.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Player.score = 0;
                game.pickScreen(5);
            }
        });

        Label.LabelStyle headingStyle = new Label.LabelStyle(white, new Color(91f/256f,75f/256f,58f/256f, 1));
        Label.LabelStyle headingStyle2 = new Label.LabelStyle(black, new Color(91f/256f,75f/256f,58f/256f, 1));


        Label heading = new Label("Score", headingStyle2);
        Label carbonStar = new Label("Carbon Score " + String.format("%d", Player.carbon), headingStyle);
        carbonStar.setAlignment(Align.center);
        carbonStar.setFontScale(2);
        Label scoreStar = new Label("Gem Score " + String.format("%d", Player.score), headingStyle);
        scoreStar.setAlignment(Align.center);
        scoreStar.setFontScale(2);
        Label bonusStar = new Label("Bonus Score " + String.format("%03d", Player.energy), headingStyle);
        bonusStar.setAlignment(Align.center);
        bonusStar.setFontScale(2);
        Label totalStar = new Label("Total Score " + String.format("%03d", Player.energy - Player.carbon / 10), headingStyle);
        totalStar.setAlignment(Align.center);
        totalStar.setFontScale(2);

        table.add();
        table.add(heading);
        table.row();

        table.add(carbonStar).width(table.getWidth()/5);
        table.add(scoreStar).width(table.getWidth()/5);
        table.add(bonusStar).width(table.getWidth()/5);

        table.row();
        table.add();
        table.add(totalStar);

        table.row();
        table.add();
        Table starTable = new Table(skin);
        if(Player.carbon>100){starTable.add(star_no_carbon).pad(15);}else{starTable.add(star_carbon).pad(15);}
        if(Player.score<300){starTable.add(star_no_gem).pad(15);}else{starTable.add(star_gem).pad(15);}
        if(Player.energy<50){starTable.add(star_no_energy).pad(15);}else{starTable.add(star_energy).pad(15);}
        table.add(starTable);
        table.row();
        table.padLeft(60);
        table.padBottom(50);
        table.add();
        table.add(buttonMain);
        table.getCell(buttonMain).spaceTop(40);
        table.row();
        table.add();
        table.add(buttonReplay);
        table.getCell(buttonReplay).spaceTop(40);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
