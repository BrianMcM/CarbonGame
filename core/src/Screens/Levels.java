package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Levels implements Screen {
    private Stage stage;
    private Table table;
    private Skin skin;
    private TextureAtlas atlas;
    private List.ListStyle listStyle;
    private List list;
    private ScrollPane scrollPane;
    private TextButton play,back;
    private java.lang.String[] stringy;
    private Texture texture;
    private BitmapFont white;
    private BitmapFont black;

    @Override
    public void show() {

//        texture = new Texture(Gdx.files.internal("knob.png"));
//        listStyle = new List.ListStyle(new BitmapFont(), Color.BLACK, new Color(1, 1, 1, 1), new TextureRegionDrawable(new TextureRegion(texture)));
//        list = new List(listStyle);
//        stringy = new java.lang.String[]{"Axe", "Fuel", "Helmet", "Flux Capacitor", "Shoes", "Hamster", "Hammer", "Pirates Eye", "Cucumber"};
//        list.setItems(stringy);
        texture = new Texture(Gdx.files.internal("knob.png"));
        listStyle = new List.ListStyle(new BitmapFont(), Color.BLACK, new Color(1, 1, 1, 1), new TextureRegionDrawable(new TextureRegion(texture)));
        list = new List<>(listStyle);
        stringy = new java.lang.String[]{"Axe", "Fuel", "Helmet", "Flux Capacitor", "Shoes", "Hamster", "Hammer", "Pirates Eye", "Cucumber"};
        list.setItems(stringy);

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
        table.debug();


//        scrollPane = new ScrollPane(list,skin);

        play = new TextButton("Play",skin);
        play.pad(15);
        back = new TextButton("Back",skin);
        back.pad(10);

        //Putting stuff in table
        table.add("Select Level");

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
        table.setDebug(true);
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
    }
}
