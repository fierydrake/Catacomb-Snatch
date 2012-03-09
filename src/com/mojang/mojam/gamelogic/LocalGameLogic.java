package com.mojang.mojam.gamelogic;

import java.io.IOException;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.Keys;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.SimpleGameElement;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.level.tile.Tile;

public class LocalGameLogic extends SimpleGameElement implements GameLogic {
	private Level level = null;
	private DifficultyInformation difficulty;

	private Player[] players;
	private Player localPlayer;
	
	public LocalGameLogic(GameCharacter character, LevelInformation levelInfo, DifficultyInformation difficulty, GameMode gameMode) 
	throws IOException {
		this.difficulty = difficulty;
		level = gameMode.generateLevel(levelInfo);
		localPlayer = new Player(level.width * Tile.WIDTH / 2 -16,
								 (level.height - 5 - 1) * Tile.HEIGHT - 16,
								 Team.Team1, CatacombSnatch.input, character); // TODO Should get player start position from level
		localPlayer.setFacing(4);
		level.addEntity(localPlayer);
		localPlayer.setCanSee(true);
		
		players = new Player[] { localPlayer };
	}
	
	@Override public Level getLevel() { return level; }
	
	@Override public DifficultyInformation getDifficulty() { return difficulty; }
	@Override public void setDifficulty(DifficultyInformation difficulty) { this.difficulty = difficulty; }
	
	@Override public Player getLocalPlayer() { return localPlayer; } // FIXME ?
	@Override public Player[] getPlayers() { return players; }

	/*
	 * Game logic
	 */
	private int mouseHideTime = 0;
	@Override
	public void tick(GameInput input) {
		Keys keys = input.getKeys();
		MouseButtons mouseButtons = input.getMouseButtons();
		
		level.tick();
		
		sound.setListenerPosition((float) localPlayer.pos.x, (float) localPlayer.pos.y);

		if (input.getMouseMoved()) {
			mouseHideTime = 0;
			if (mouseButtons.mouseHidden) {
				mouseButtons.mouseHidden = false;
			}
		}
		if (mouseHideTime < 60) {
			if (++mouseHideTime == 60) {
				mouseButtons.mouseHidden = true;
			}
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
