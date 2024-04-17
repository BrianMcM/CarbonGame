package com.carbon.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setResizable(false);
		//FULLSCREEN
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setWindowedMode(1600, 896);
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setTitle("Carbon Game");
		new Lwjgl3Application(new CarbonGame(), config);
	}
}