package com.carbon.game;

import Screens.Splash;
import com.badlogic.gdx.Game;

public class CarbonGame extends Game {

	public void create () {
		setScreen(new Splash());
	}

	public void render () {
		super.render();
	}

	public void dispose () {
	}
}
