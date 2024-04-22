package Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class Levels implements Screen {
    private Stage stage;
    private Table table;
    private Skin skin;
    private TextureAtlas atlas;

    @Override
    public void show() {
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);
        BitmapFont white = new BitmapFont();
        white.setColor(256, 256, 256, 1);

        BitmapFont black = new BitmapFont();
        black.setColor(0, 0,0, 1);

        atlas = new TextureAtlas("ui/inbetween.txt");
        skin = new Skin(atlas);

        table = new Table(skin);
        table.setBounds(0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        table.debug();

        TextButton play = new TextButton("Play", skin);
        play.pad(15);
        TextButton back = new TextButton("Back", skin);
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
