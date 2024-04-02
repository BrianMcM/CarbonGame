package com.carbon.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.carbon.game.CarbonGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1024, 768);
		config.setResizable(false);
		//FULLSCREEN
		//config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.useVsync(true);
		config.setForegroundFPS(60);
		config.setTitle("Carbon Game");
		new Lwjgl3Application(new CarbonGame(), config);
	}
}
