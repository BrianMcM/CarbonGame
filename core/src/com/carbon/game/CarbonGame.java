package com.carbon.game;

import Screens.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CarbonGame extends Game {
	public SpriteBatch batch;
	public BitmapFont font;
//	private GameScreen gameScreen;

	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		setScreen(new MainMenu());
	}

	public void render () {
		super.render();
	}

//	public Screen setGameScreen() {
//		gameScreen = new GameScreen();
//		this.setScreen(gameScreen);
//		return null;
//	}

	public void dispose () {
		batch.dispose();
		font.dispose();
//		gameScreen.dispose();
	}
}
