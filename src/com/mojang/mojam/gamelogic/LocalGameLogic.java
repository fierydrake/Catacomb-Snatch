package com.mojang.mojam.gamelogic;

import static com.mojang.mojam.CatacombSnatch.menus;
import static com.mojang.mojam.CatacombSnatch.sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.PlayerInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.LogicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.menus.PauseMenu;
import com.mojang.mojam.gui.menus.WinMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;
import com.mojang.mojam.math.Facing;
import com.mojang.mojam.screen.Screen;

public class LocalGameLogic implements GameLogic {
	private Level level;
	private GameInformation game;

	private List<Player> players = new ArrayList<Player>();
	private List<GameView> views = new ArrayList<GameView>();
	
	public LocalGameLogic(GameInformation game) 
	throws IOException {
		this.game = game;
		level = game.generateLevel();
		
		// For now we should only expect 1 local player; although split-screen multiplayer may be in the offing.
		//
		// With the current set-up we are limited to one view per player (at game start -- we could always add
		// code to add extra views of a player for observer mode or spying device etc, and it is probably better
		// and more useful to do this on-the-fly (ie after game start)).
		for (PlayerInformation playerInfo : game.players) {
			Player player = new Player(level.width * Tile.WIDTH / 2 -16, 
		                               (level.height - 5 - 1) * Tile.HEIGHT - 16,
		                               playerInfo.team, playerInfo.character, players.size() == 0 ? 4 /* NORTH */: 0 /* SOUTH */,
		                               playerInfo.input);
			player.setCanSee(true); // FIXME: Looks like we have a per-logic fog of war, rather than per-player :( Local players share fog of war
			players.add(player);
			playerInfo.view.setPlayer(player);
			views.add(playerInfo.view);
			level.addEntity(player);
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
		level.tick();

		// FIXME Don't know how to set more than one listener position to hear sounds for all players (this is a local game after all)
		//       Maybe we need a GameSound for each player? And no-op GameSounds for remote players in network games...?
		sound().setListenerPosition((float) players.get(0).pos.x, (float) players.get(0).pos.y); 
		for (Player player : players) {
			LogicalInputs inputs = player.getInput().getCurrentState();
			
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
// TOOD A bunch of code to be checked off the list (much of this is not for local game logic)
//		
//		if (packetLink != null) {
//			packetLink.tick();
//		}
//
//		if (!paused) {
//			for (int index = 0; index < keys.getAll().size(); index++) {
//				Keys.Key key = keys.getAll().get(index);
//				boolean nextState = key.nextState;
//				if (key.isDown != nextState) {
//					synchronizer.addCommand(new ChangeKeyCommand(index, nextState));
//				}
//			}
//
//			keys.tick();
//			for (Keys skeys : synchedKeys) {
//				skeys.tick();
//			}
//
//			if (keys.pause.wasPressed()) {
//				keys.release();
//				mouseButtons.releaseAll();
//				synchronizer.addCommand(new PauseCommand(true));
//			}
//
//			level.tick();
//			if (isMultiplayer) {
//				tickChat();
//			}
//		}
//
//		if (keys.screenShot.isDown) {
//			takeScreenShot();
//		}
	}
}
