package com.mojang.mojam;

import java.io.IOException;
import java.util.ArrayDeque;

import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gamelogic.GameLogic;
import com.mojang.mojam.gamelogic.LocalGameLogic;
import com.mojang.mojam.gamelogic.NoGameLogic;
import com.mojang.mojam.gameloop.SimpleGameLoop;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.menus.CharacterSelectionMenu;
import com.mojang.mojam.gui.menus.GuiError;
import com.mojang.mojam.gui.menus.GuiMenu;
import com.mojang.mojam.gui.menus.TitleMenu;

public class GameMenus implements Runnable {
	private ArrayDeque<GuiMenu> stack = new ArrayDeque<GuiMenu>();
	
	public boolean isShowing() { return !stack.isEmpty(); }
	public GuiMenu getCurrent() { return stack.peek(); }
	public void push(GuiMenu menu) { stack.push(menu); }
	public GuiMenu pop() { return stack.pop(); }
	public void clear() { stack.clear(); }
	
	private GameInformation nextGameInformation = new GameInformation();
	private LocalGameInput localInput;
	private GameView localView;
	private GameLogic logic;
	
	private long nextMusicInterval;
	private boolean mouseActive = false;
	private int mouseActiveTime = 0;
	
	public GameMenus(LocalGameInput localInput, GameView localView) {
		this.localInput = localInput;
		this.localView = localView;
		logic = new NoGameLogic();
	}
	
	public boolean isMouseActive() { return mouseActive; }
	public boolean isPlayingGame() { return !(logic instanceof NoGameLogic); }
	public LocalGameInput getLocalInput() { return localInput; }
	public GameLogic getGameLogic() { return logic; }
	public GameInformation getGameInformation() { 
		return isPlayingGame() ? logic.getGameInformation() : nextGameInformation; 
	}
	
	public void startPlaying(boolean multiplayer) {
		// FIXME multiplayer!
		try {
			if (!multiplayer) {
				nextGameInformation.players.add(new PlayerInformation(Team.Team1, CatacombSnatch.selectedCharacter, (GameInput)localInput, localView));
			}
			logic = multiplayer ? logic 
					: new LocalGameLogic(
							nextGameInformation,
							localInput,
							CatacombSnatch.selectedCharacter);
			clear();
			CatacombSnatch.sound.startBackgroundMusic();
			nextMusicInterval = (System.currentTimeMillis() / 1000) + 4 * 60;
		} catch (IOException e) {
			e.printStackTrace();
			push(new GuiError("Could not load game level"));
		}
	}
	
	public void stopPlaying() {
		logic = new NoGameLogic();
		clear();
		CatacombSnatch.sound.stopBackgroundMusic();
		CatacombSnatch.sound.startTitleMusic();
		push(new TitleMenu());
	}
	
	public void exitGame() {
		CatacombSnatch.stop();
		System.exit(0);
	}
	
	public void run() {
		/*
		 * Initialise menus
		 */
		CatacombSnatch.sound.startTitleMusic();
		push(new TitleMenu());
		if (!Options.isCharacterIDset()) {
			push(new CharacterSelectionMenu());
		}
		
		/* 
		 * Game loop
		 */
		new SimpleGameLoop(60, new Runnable() {
			/* Render callback */
			@Override
			public void run() {
				localView.renderView(localInput, GameMenus.this, logic);
			}
		}, new Runnable() {
			/* Logic callback */
			@Override
			public void run() {
				if (isShowing()) tick(localInput);
				else if (isPlayingGame()) logic.tick(localInput);
				
				// Background music
				if (isPlayingGame()) {
					if (System.currentTimeMillis() / 1000 > nextMusicInterval) {
						nextMusicInterval = (System.currentTimeMillis() / 1000) + 4 * 60;
						CatacombSnatch.sound.startBackgroundMusic();
					}	
				}
				
				// Input
				localInput.gatherInput();
				if (localInput.getCurrentPhysicalState().wasMouseMoved()) {
					mouseActive = true;
					mouseActiveTime = 60;
				} else if (--mouseActiveTime <= 0) {
					mouseActive = false;
				} 
			}
		}).start();
	}

	public void tick(LocalGameInput input) {
		getCurrent().tick(input);
	}
}
