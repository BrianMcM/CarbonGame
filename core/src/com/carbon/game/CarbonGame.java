package com.carbon.game;

import Screens.LevelsScreen;
import com.badlogic.gdx.Game;
import Screens.MainMenu;
import Screens.ScoreScreen;
import Screens.Splash;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;

public class CarbonGame extends Game {

  public static int SCREENUMBER;

	public void create () {
		pickScreen(3);
	}

	public void pickScreen(int screenNum) {
		Screen newScreen;
		SCREENUMBER = screenNum;
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
				newScreen = new GameScreen(this, "Maps/map_tutorial.tmx", "Maps/metro_tutorial.tmx", 100,"      Tutorial");
				SCREENNUMBER = screenNum;
				break;
			default:
				newScreen = new GameScreen(this, "Maps/map.tmx", "Maps/metro.tmx", 100, "      Level ONE");
				SCREENNUMBER = screenNum;
		}
		screenChange(newScreen);
	}

	private void screenChange(Screen newScreen) {
		setScreen(newScreen);
	}

	public void render () {
		super.render();
	}

	public void dispose () {
		currentScreen.dispose();
	}
}
