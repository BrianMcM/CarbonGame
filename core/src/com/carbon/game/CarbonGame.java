package com.carbon.game;

import Screens.MainMenu;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CarbonGame extends Game {

	public void create () {
		setScreen(new MainMenu());
	}

	public void render () {
		super.render();
	}

	public void dispose () {
	}
}
