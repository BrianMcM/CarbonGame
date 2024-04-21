package com.carbon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScoreScreen extends ScreenClass implements Screen {
    private final CarbonGame game;
    private final OrthographicCamera camera;
    float width;
    float height;
    private final Texture sprite = new Texture(Gdx.files.internal("testMap/tilemap_packed.png"));
    private final ScoreManager scoreManager;
    public ScoreScreen(final CarbonGame game, int score, int carbon) {
        this.game = game;

        camera = new OrthographicCamera();

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        camera.setToOrtho(false, width, height);

        scoreManager = new ScoreManager();
        int final_score = calculateScore(score, carbon);
        saveScore(final_score);
    }

    private int calculateScore(int score, int carbon) {
        return score - carbon;
    }

    private void saveScore(int score) {
        scoreManager.saveScore("test", String.valueOf(score));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);
        game.batch.setProjectionMatrix(camera.combined);
        //sprite batch
        game.batch.begin();
        game.batch.draw(sprite, width/2, height/2, 432, 288);
        game.batch.end();
    }

    @Override
    public void show() {

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
        sprite.dispose();
    }
}
