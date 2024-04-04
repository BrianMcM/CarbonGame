package com.carbon.game;

import Screens.GameScreen;
import Screens.Splash;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
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
		setScreen(new Splash());
//		try {
//			// Perform first task
//			System.out.println("Task 1: Performing task...");
//			setScreen(new Splash());
//
//			// Sleep for 2 seconds (2000 milliseconds)
//			Thread.sleep(10000);
//
//			// Perform second task
//			System.out.println("Task 2: Performing another task...");
//			setGameScreen();
//		} catch (InterruptedException e) {
//			// Handle interrupted exception if necessary
//			e.printStackTrace();
//		}
	//setGameScreen();
	}

	public void render () {
		super.render();
	}

	public Screen setGameScreen() {
		gameScreen = new GameScreen();
		this.setScreen(gameScreen);

		return null;
	}


	public void dispose () {
		batch.dispose();
		font.dispose();
		gameScreen.dispose();
	}
}
