package com.carbon.game;

import Screens.LevelsScreen;
import com.badlogic.gdx.Game;
import Screens.MainMenu;
import Screens.ScoreScreen;
import Screens.Splash;
import com.badlogic.gdx.Screen;

public class CarbonGame extends Game {

	private Screen currentScreen = null;

	public void create () {
		pickScreen(0);
	}

	public void pickScreen(int screenNum) {
		Screen newScreen;
		switch (screenNum) {
			case 0:
				newScreen = new Splash(this);
				break;
			case 1:
				newScreen = new MainMenu(this);
				break;
			case 2:
				newScreen = new LevelsScreen(this);
				break;
			case 3:
				newScreen = new ScoreScreen(this);
				break;
			case 4:
				newScreen = new GameScreen(this, "Maps/map_tutorial.tmx", "Maps/metro_tutorial.tmx", 200);
				break;
			default:
				newScreen = new GameScreen(this, "Maps/map.tmx", "Maps/metro.tmx", 200);
		}
		screenChange(newScreen);
	}

	private void screenChange(Screen newScreen) {
		setScreen(newScreen);
		if (currentScreen != null) {
			currentScreen.dispose();
		}
		currentScreen = newScreen;
	}

	public void render () {
		super.render();
	}

	public void dispose () {
	}
}
