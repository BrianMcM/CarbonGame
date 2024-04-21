package com.carbon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CarbonGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
    public ScreenClass currentScreen;

	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		setGameScreen();
	}

	public void render () {
		super.render();
	}

	public void setGameScreen() {
        GameScreen gameScreen = new GameScreen(this, 10);
		this.setScreen(gameScreen);
		currentScreen = gameScreen;
	}

	public void setScoreScreen(int score, int carbon) {
		ScoreScreen scoreScreen = new ScoreScreen(this, score, carbon);
		this.setScreen(scoreScreen);
		currentScreen = scoreScreen;
	}

	public void dispose () {
		batch.dispose();
		font.dispose();
		currentScreen.dispose();
	}
}
