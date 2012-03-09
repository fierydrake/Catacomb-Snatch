package com.mojang.mojam;

import java.awt.Canvas;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.StandaloneLauncher.GameWindow;
import com.mojang.mojam.gamelogic.GameLogic;
import com.mojang.mojam.gamelogic.GameMenus;
import com.mojang.mojam.gamelogic.NullGameLogic;
import com.mojang.mojam.gamesound.GameSound;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gameview.MenuInputHandler;
import com.mojang.mojam.gameview.SimpleGameView;
import com.mojang.mojam.gui.CharacterSelectionMenu;
import com.mojang.mojam.gui.TitleMenu;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.sound.SoundPlayer;

/*
 * This is the orchestrating class for Catacomb Snatch
 */
public class CatacombSnatch {
	public static final String EXTERNALS_DIRNAME = ".catacomb_snatch" + File.separator;
	
	/* For use by launchers */
	public static final Canvas canvas = new CatacombSnatchCanvas(GameView.WIDTH * GameView.SCALE, GameView.HEIGHT * GameView.SCALE);
	
	/* For use by all game elements */
	public static final GameSound sound = new SoundPlayer();
	public static final GameMenus menus = new GameMenus();
	private static GameLogic logic = new NullGameLogic();
	public static GameLogic logic() { return logic; }
	
	/* For use locally by SimpleGameView */
	private static final ScreenRenderer renderer = (ScreenRenderer)canvas;
	
	/* For use locally by Game thread (and the KeyBindingsMenu for input) */
	public static final GameInput input = (GameInput)canvas;
	private static final List<GameView> views = new ArrayList<GameView>();

	public static void init() {
		Options.loadProperties();
		Texts.setLocale(Options.get(Options.LOCALE, "en"));
	}

	public static void start() {
		menus.push(new TitleMenu());
		if(!Options.isCharacterIDset()){
			menus.push(new CharacterSelectionMenu());
		}
		input.registerMenuInputHandler(new MenuInputHandler(menus));
		views.add(new SimpleGameView(menus, logic, renderer));
		
		new Thread("Game thread") {
			@Override 
			public void run() {
				while (true) {
					/* Gather input */
					input.gatherInput();
					/* Render */
					for (GameView view : views) {
						view.renderView();
					}
					/* Tick logic */
					if (menus.isShowing()) {
						menus.tick(input);

					} else if (logic != null && logic.isPlayingLevel()) {
						logic.tick(input);
						
					} else {
						throw new RuntimeException();
					}
				}
			}
		}.start();
	}
	
	public static void stop() {
		// TODO (For applet)
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

