package com.carbon.game;

import com.badlogic.gdx.Game;
import Screens.MainMenu;
import Screens.ScoreScreen;
import Screens.Splash;


public class CarbonGame extends Game {

	public void create () {
//		setScreen(new Splash());
//		setScreen(new MainMenu());
		setScreen(new ScoreScreen());

	}

	public void render () {
		super.render();
	}

	public void dispose () {
	}
}
