package com.mojang.mojam.gamelogic;

import java.io.IOException;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.gameinput.LogicalInputs;
import com.mojang.mojam.gamesound.GameSound;
import com.mojang.mojam.gui.menus.PauseMenu;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.tile.Tile;

public class LocalGameLogic implements GameLogic {
	protected final GameSound sound = CatacombSnatch.sound;
	
	private Level level = null;
	private GameInformation gameInformation;

	private Player[] players;
	private Player localPlayer;
	
	public LocalGameLogic(GameInformation gameInformation, GameInput input, GameCharacter character) 
	throws IOException {
		this.gameInformation = gameInformation;
		level = gameInformation.generateLevel();
		localPlayer = new Player(level.width * Tile.WIDTH / 2 -16,
								 (level.height - 5 - 1) * Tile.HEIGHT - 16,
								 Team.Team1, this, input, character); // TODO Should get player start position from level
		localPlayer.setFacing(4);
		level.addEntity(localPlayer);
		localPlayer.setCanSee(true);
		
		players = new Player[] { localPlayer };
	}
	
	@Override public Level getLevel() { return level; }
	
	@Override public GameInformation getGameInformation() { return gameInformation; }
	
	@Override public Player getLocalPlayer() { return localPlayer; } // FIXME ?
	@Override public Player[] getPlayers() { return players; }
	
	@Override public boolean isMouseActive() { return CatacombSnatch.menus.isMouseActive() ; }

	/*
	 * Game logic
	 */
	@Override
	public void tick(GameInput input) {
		LogicalInputs inputs = input.getCurrentState();
		
		level.tick();
		
		sound.setListenerPosition((float) localPlayer.pos.x, (float) localPlayer.pos.y);

		if (inputs.pause.wasPressed) {
//			keys.release();
//			mouseButtons.releaseAll();
//			synchronizer.addCommand(new PauseCommand(true));
			CatacombSnatch.menus.push(new PauseMenu()); // FIXME
		}
//		if (level != null && level.victoryConditions != null) {
//			if (level.victoryConditions.isVictoryConditionAchieved()) {
//				int winner = level.victoryConditions.playerVictorious();
//				GameCharacter winningCharacter = winner == players[0].getTeam() ? players[0].getCharacter()
//						: players[1].getCharacter();
//				addMenu(new WinMenu(GAME_WIDTH, GAME_HEIGHT, winner, winningCharacter));
//                level = null;
//                return;
//            }
//        }
//		
//		if (packetLink != null) {
//			packetLink.tick();
//		}


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
//		// every 4 minutes, start new background music :)
//		if (System.currentTimeMillis() / 1000 > nextMusicInterval) {
//			nextMusicInterval = (System.currentTimeMillis() / 1000) + 4 * 60;
//			soundPlayer.startBackgroundMusic();
//		}
//
//		if (keys.screenShot.isDown) {
//			takeScreenShot();
//		}
	}
}
