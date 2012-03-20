package com.mojang.mojam;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gamelogic.GameLogic;
import com.mojang.mojam.gamelogic.LocalGameLogic;
import com.mojang.mojam.gamelogic.NoGameLogic;
import com.mojang.mojam.gamelogic.SyncClientGameLogic;
import com.mojang.mojam.gamelogic.SyncServerGameLogic;
import com.mojang.mojam.gameloop.SimpleGameLoop;
import com.mojang.mojam.gamesound.GameSound;
import com.mojang.mojam.gamesound.SoundPlayer;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.menus.CharacterSelectionMenu;
import com.mojang.mojam.gui.menus.GuiError;
import com.mojang.mojam.gui.menus.TitleMenu;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/*
 * This is the orchestrating class for Catacomb Snatch
 */
public class CatacombSnatch {
	public static final int DEFAULT_PORT = 3000;
	private static final String EXTERNALS_DIRNAME = ".catacomb_snatch" + File.separator;
	private static final int RENDERER_SCALE = 2;
	private static final Dimension RENDERER_SIZE = new Dimension(GameView.WIDTH * RENDERER_SCALE, GameView.HEIGHT * RENDERER_SCALE);

	private static Launcher launcher;
	private static GameSound sound;
	private static GameMenus menus;
	private static GameLogic logic;
	private static long nextMusicInterval;
	
	private static Screen screen = new Screen(GameView.WIDTH, GameView.HEIGHT);
	private static ScreenRenderer renderer;
	private static LocalGameInput localInput;
	
	private static GameInformation nextGame = new GameInformation();
	
	public static GameSound sound() { return sound; }
	public static GameMenus menus() { return menus; }
	public static GameLogic logic() { return logic; }
	public static GameInformation game() { return isPlayingGame() ? logic.getGame() : nextGame; }
	public static LocalGameInput getLocalInput() { return localInput; }
	public static boolean isPlayingGame() { return logic.isPlaying(); }
	public static boolean isGamePaused() { return menus.isShowing(); }
	
	public static Dimension getRendererSize() { return RENDERER_SIZE; }
	public static int getRendererScale() { return RENDERER_SCALE; }
	public static boolean getFullscreen() { 
		return Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE);
	}
	
	public static void toggleFullscreen() { launcher.toggleFullscreen(); }
	public static void setFullscreen(boolean fullscreen) { launcher.setFullscreen(fullscreen); }
	public static void setRendererSize(Dimension size) { launcher.setRendererSize(size); }
	public static void setRendererScale(int scale) { launcher.setRendererScale(scale); }
	
	public static void start(Launcher launcher, LocalGameInput localInput, ScreenRenderer renderer) {
		CatacombSnatch.launcher = launcher;
		CatacombSnatch.localInput = localInput;
		CatacombSnatch.renderer = renderer;
		sound = new SoundPlayer();
		menus = new GameMenus(localInput);
		logic = new NoGameLogic();
		
		sound.startTitleMusic();
		setupInitialMenus();
		startGameLoop();
	}
	
	public static void stop() {
		sound.stopBackgroundMusic();
		sound.shutdown();
	}

	public static void exit() {
		stop();
		System.exit(0);
	}

	private static void setupInitialMenus() {
		menus.push(new TitleMenu());
		if (!Options.isCharacterIDset()) {
			menus.push(new CharacterSelectionMenu());
		}
	}
	
	private static void startGameLoop() {
		SimpleGameLoop gameLoop = new SimpleGameLoop(60, new Runnable() {
			/* Render callback */
			@Override
			public void run() {
				/* Render all GameViews to the Screen */
				if (isPlayingGame()) logic.renderViews(screen);
				/* Render menus to the Screen */
				if (menus.isShowing()) menus.render(screen);
				/* Render the mouse cursor to the Screen */
				if (localInput.isMouseActive()) {
					renderMouse(screen, localInput.getMousePosition());
				}
				/* Render the Screen in the ScreenRenderer */
				renderer.render(screen);
			}
		}, new Runnable() {
			/* Logic callback */
			@Override
			public void run() {
				/* Tick menus or tick game */
				if (menus.isShowing()) menus.tick();
				if (isPlayingGame()) logic.tick();
				
				/* Background music */
				if (isPlayingGame()) {
					if (System.currentTimeMillis() / 1000 > nextMusicInterval) {
						nextMusicInterval = (System.currentTimeMillis() / 1000) + 4 * 60;
						CatacombSnatch.sound.startBackgroundMusic();
					}	
				}
			}
		});
		new Thread(gameLoop, "Game thread").start();
	}
	
	public static void startGame() {
		try {
			switch (nextGame.type) {
			case SINGLE_PLAYER: 
				logic = new LocalGameLogic(nextGame); 
				break;
			case SYNCHED_NETWORK:
				if (nextGame.networkInformation.isHost())
					logic = new SyncServerGameLogic(nextGame);
				else
					logic = new SyncClientGameLogic(nextGame);
				break;
			default:
				throw new UnsupportedOperationException();
			}
			/* Close all menus; game logic should start ticking */
			menus.clear();
			/* Start game music */
			CatacombSnatch.sound.startBackgroundMusic();
			nextMusicInterval = (System.currentTimeMillis() / 1000) + 4 * 60;
		} catch (IOException e) {
			e.printStackTrace();
			menus.push(new GuiError("Failed to start game: could not load game level"));
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			menus.push(new GuiError("Failed to start game: game type not supported (" + nextGame.type + ")"));
		}
	}
	
	public static void stopGame() {
		logic = new NoGameLogic();
		nextGame = new GameInformation();
		menus.clear();
		sound.stopBackgroundMusic();
		sound.startTitleMusic();
		menus.push(new TitleMenu());
	}
	
	private static void renderMouse(Screen screen, Point pos) {
		int crosshairSize = 15;
		int crosshairSizeHalf = crosshairSize / 2;

		Bitmap marker = new Bitmap(crosshairSize, crosshairSize);

		// horizontal line
		for (int i = 0; i < crosshairSize; i++) {
			if (i >= crosshairSizeHalf - 1 && i <= crosshairSizeHalf + 1)
				continue;

			marker.pixels[crosshairSizeHalf + i * crosshairSize] = 0xffffffff;
			marker.pixels[i + crosshairSizeHalf * crosshairSize] = 0xffffffff;
		}

		screen.blit(marker, pos.x - crosshairSizeHalf - 2, pos.y - crosshairSizeHalf - 2);
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
}

