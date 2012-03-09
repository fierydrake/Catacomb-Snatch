package com.mojang.mojam.gamelogic;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.Keys;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;

public class LocalGameLogic implements GameLogic {
	private Level currentLevel = null;
	
	private GameCharacter selectedCharacter;
	private LevelInformation selectedLevel;
	private DifficultyInformation selectedDifficulty;
	private GameMode selectedGameMode;
	
	private Player[] players;
	private Player localPlayer;
	
	public LocalGameLogic() {
		selectedCharacter = GameCharacter.values()[Options.getCharacterID()];
	}
	
	@Override public boolean isPlayingLevel() { return currentLevel != null; }
	public Level getCurrentLevel() { return currentLevel; }
	
	@Override public GameCharacter getSelectedCharacter() { return selectedCharacter; }
	@Override public LevelInformation getSelectedLevel() { return selectedLevel; }
	@Override public DifficultyInformation getSelectedDifficulty() { return selectedDifficulty; }
	public GameMode getSelectedGameMode() { return selectedGameMode; }
	
	@Override public void setSelectedCharacter(GameCharacter character) { selectedCharacter = character; }
	@Override public void setSelectedLevel(LevelInformation level) { selectedLevel = level; }
	@Override public void setSelectedDifficulty(DifficultyInformation difficulty) { selectedDifficulty = difficulty; }
	public void setSelectedGameMode(GameMode mode) { selectedGameMode = mode; }
	
	@Override public Player getLocalPlayer() { return localPlayer; } // FIXME ?
	@Override public Player[] getPlayers() { return players; }

	/*
	 * Game logic
	 */
	@Override
	public void tick(GameInput input) {
		Keys keys = input.getKeys();
		MouseButtons mouseButtons = input.getMouseButtons();
		
//		if (menus.isShowing()) {
//			menus.getCurrent().tick(mouseButtons);
//		}
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
