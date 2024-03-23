package com.carbon.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CarbonGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
	private GameScreen gameScreen;

	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		//this.setScreen(new MenuScreen(this));
		setGameScreen();
	}

	public void render () {
		super.render();
	}

	public void setGameScreen() {
		gameScreen = new GameScreen(this);
		this.setScreen(gameScreen);
	}

	public void dispose () {
		batch.dispose();
		font.dispose();
		gameScreen.dispose();
	}
}
