package Screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import Screens.Tween.SpriteAccessor;
import com.carbon.game.CarbonGame;

public class Splash implements Screen {
    private Sprite splash;
    private SpriteBatch batch;
    private TweenManager tweenManager;
    private final CarbonGame game;

    public Splash(CarbonGame g) {
        game = g;
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tweenManager.update(delta);

        batch.begin();
        splash.draw(batch);
        batch.end();
    }
    @Override
    public void show() {

        tweenManager = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        Texture splashTexture = new Texture("uiskin/CarbonWorld.png");

        splash = new Sprite(splashTexture);
        splash.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        Tween.set(splash,SpriteAccessor.Alpha).target(0).delay(1).start(tweenManager);
        Tween.to(splash,SpriteAccessor.Alpha,2).target(1).start(tweenManager);
        Tween.to(splash,SpriteAccessor.Alpha,2).target(0).delay(2).setCallback(new TweenCallback() {

            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                game.pickScreen(1);
            }
        }).start(tweenManager);
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
        dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        splash.getTexture().dispose();
    }
}
