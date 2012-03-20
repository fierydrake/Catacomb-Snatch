package com.mojang.mojam.gamelogic;

import static com.mojang.mojam.CatacombSnatch.menus;
import static com.mojang.mojam.CatacombSnatch.sound;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.PlayerInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.gameinput.LogicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.menus.PauseMenu;
import com.mojang.mojam.gui.menus.WinMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.screen.Screen;

public class LocalGameLogic implements GameLogic {
	public static final int MAX_PLAYERS = 2;
	
	protected Level level;
	protected GameInformation game;

	protected List<Player> players = new ArrayList<Player>();
	protected List<GameView> views = new ArrayList<GameView>();
	
	protected LocalGameLogic() {}
	
	public LocalGameLogic(GameInformation game) 
	throws IOException {
		this.game = game;

		level = game.generateLevel();
		
		// With the current set-up we are limited to one view per player (at game start -- we could always add
		// code to add extra views of a player for observer mode or spying device etc, and it is probably better
		// and more useful to do this on-the-fly (ie after game start)).
		for (PlayerInformation playerInfo : game.players) {
			addPlayerInNextSlot(playerInfo);
		}
	}

	protected void addPlayerInNextSlot(PlayerInformation playerInfo) {
		int playerId = players.size();
		addOrUpdatePlayer(playerId, playerInfo);
	}

	protected void addOrUpdatePlayer(int playerId, PlayerInformation playerInfo) {
		if (playerId < players.size() && players.get(playerId) != null) {
			/* Player slot is valid, update the player */
			Player player = players.get(playerId);
			player.setCharacter(playerInfo.character);
		} else {
			while (playerId >= players.size()) {
				players.add(null);
			}
			Point start = getStartPosition(playerId);
			int facing = getPlayerFacing(playerId);
			System.err.println("Adding player");
			System.err.println("- X: " + start.x);
			System.err.println("- Y: " + start.y);
			System.err.println("- T: " + playerInfo.team);
			System.err.println("- C: " + playerInfo.character);
			System.err.println("- F: " + facing);
			System.err.println("- I: " + playerInfo.input.getClass().getSimpleName());
			Player player = new Player(start.x, start.y, playerInfo.team, playerInfo.character, 
					facing, playerInfo.input);
			player.setCanSee(true); // FIXME: Looks like we have a per-logic fog of war, rather than per-player :( players share fog of war
			players.set(playerId, player);
			playerInfo.view.setPlayer(player);
			views.add(playerInfo.view);
			level.addEntity(player);
		}
	}
	
	protected Point getStartPosition(int playerId) {
		switch (playerId) {
		case 0: return new Point(level.width * Tile.WIDTH / 2 -16, (level.height - 5 - 1) * Tile.HEIGHT - 16);
		case 1: return new Point(level.width * Tile.WIDTH / 2 -16, 7 * Tile.HEIGHT - 16);
		default: 
			return new Point(level.width * Tile.WIDTH / 2 -16, (level.height - 5 - 1) * Tile.HEIGHT - 16);
		}
	}
	
	protected int getPlayerFacing(int playerId) {
		switch (playerId) {
		case 0: return 4; /* NORTH */
		case 1: return 0; /* SOUTH */
		default:
			return 0;
		}
	}
	
	@Override public boolean isPlaying() { return true; } // FIXME? Probably not needed.. Depends if network game logic needs startup period (probably going to handle startup in a menu, so probably not)
	@Override public Level getLevel() { return level; }
	@Override public GameInformation getGame() { return game; }
	@Override public List<Player> getPlayers() { return players; }
	
	@Override 
	public void renderViews(Screen screen) {
		for (GameView view : views) {
			view.renderView(screen);
		}
	}
	
	/*
	 * Game logic
	 */
	@Override
	public void tick() {
		if (!CatacombSnatch.isGamePaused()) {
			level.tick();
	
			// FIXME Don't know how to set more than one listener position to hear sounds for all players (this is a local game after all)
			//       Maybe we need a GameSound for each player? And no-op GameSounds for remote players in network games...?
			sound().setListenerPosition((float) players.get(0).pos.x, (float) players.get(0).pos.y);
			Set<GameInput> uniqueGameInputs = new HashSet<GameInput>();
			for (Player player : players) {
				if (player == null) continue;
				GameInput gameInput = player.getInput();
				uniqueGameInputs.add(gameInput);
				
				LogicalInputs inputs = gameInput.getCurrentState();
				
				if (!CatacombSnatch.isGamePaused() && inputs.pause.wasPressed) {
				// FIXME? Does this even need fixing? The pause key should be sent to remote clients (and besides, this is the local game logic)
	//			keys.release();
	//			mouseButtons.releaseAll();
	//			synchronizer.addCommand(new PauseCommand(true));
					menus().push(new PauseMenu());
				}
				
			}
	
			if (level.victoryConditions != null) {
				if (level.victoryConditions.isVictoryConditionAchieved()) {
					Team winner = level.victoryConditions.playerVictorious();
					GameCharacter winningCharacter = winner == players.get(0).getTeam() ? players.get(0).getCharacter()
							: players.get(1).getCharacter(); /* FIXME needs to be less hacky */
					sound().startEndMusic();
					menus().push(new WinMenu(winner, winningCharacter));
	                return;
	            }
	        }
			
			/* Gather input for next tick */
			for (GameInput gameInput : uniqueGameInputs) {
				gameInput.tick();
			}
		}
	}
}
