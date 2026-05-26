package com.memegotchi.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.memegotchi.game.MemeGotchi;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("MemeGotchi");
		config.setWindowedMode(GameResources.SCREEN_WIDTH / 2, GameResources.SCREEN_HEIGHT / 2);
		config.disableAudio(false);
		new Lwjgl3Application(new MemeGotchi(), config);
	}
}
