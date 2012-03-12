package com.mojang.mojam;

import java.awt.Canvas;
import java.io.File;

import com.mojang.mojam.StandaloneLauncher.GameWindow;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gamesound.GameSound;
import com.mojang.mojam.gamesound.SoundPlayer;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gameview.SimpleGameView;
import com.mojang.mojam.resources.Texts;

/*
 * This is the orchestrating class for Catacomb Snatch
 */
public class CatacombSnatch {
	public static final String EXTERNALS_DIRNAME = ".catacomb_snatch" + File.separator;
	
	/* For use by launchers */
	public static final Canvas canvas = new CatacombSnatchCanvas(GameView.WIDTH * GameView.SCALE, GameView.HEIGHT * GameView.SCALE);
	
	/* For use locally by SimpleGameView */
	private static final ScreenRenderer renderer = (ScreenRenderer)canvas;

	/* For use by all game elements */
	public static final GameSound sound = new SoundPlayer();
	public static final GameMenus menus = new GameMenus(new LocalGameInput(canvas), new SimpleGameView(renderer));
	
	/* 
	 * For use by menus while setting up next game, difficulty is stored
	 * in PendingGameLogic, so that the call needed is the same
	 * whether a game is running or not. These ones can't currently be
	 * changed mid-game
	 */
	public static GameCharacter selectedCharacter;

	static {
		Texts.setLocale(Options.get(Options.LOCALE, "en"));
		selectedCharacter = GameCharacter.values()[Options.getCharacterID()];
	}

	public static void start() {
		sound.startBackgroundMusic();
		new Thread(menus, "Game thread").start();
	}
	
	public static void stop() {
		sound.stopBackgroundMusic();
		sound.shutdown();
	}
	
	public static File getExternalsDir() {
		String userHomeDir = System.getProperty("user.home", ".");
		
		File externalsDir;
		switch (OS.get()) {
		case linux:
		case solaris:
			externalsDir = new File(userHomeDir, EXTERNALS_DIRNAME);
			break;
		case windows:
			String appDataDir = System.getenv("APPDATA");
			externalsDir = new File(appDataDir != null ? appDataDir : userHomeDir, EXTERNALS_DIRNAME);
			break;
		case macos:
			externalsDir = new File(userHomeDir, "Library/Application Support/" + EXTERNALS_DIRNAME);
			break;
		default:
			externalsDir = new File(userHomeDir, EXTERNALS_DIRNAME);
			break;
		}
		if (!externalsDir.exists() && !externalsDir.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + externalsDir);
		}
		return externalsDir;
	}
	
	public static void toggleFullscreen() {
		if (canvas.getParent() instanceof GameWindow) {
			((GameWindow)canvas.getParent()).toggleFullscreen();
		}
	}
	public static void setFullscreen(boolean fullscreen) {
		if (canvas.getParent() instanceof GameWindow) {
			((GameWindow)canvas.getParent()).setFullscreen(fullscreen);
		}
	}
}

