package com.mojang.mojam.gamelogic;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.LevelInformation;

public class NullGameLogic implements GameLogic {
	@Override public boolean isPlayingLevel() { return false; }
	
	@Override public Player[] getPlayers() { return null; }
	@Override public Player getLocalPlayer() { return null; }
	
	@Override public DifficultyInformation getSelectedDifficulty() { return null; }
	@Override public void setSelectedDifficulty(DifficultyInformation difficulty) {}
	
	@Override public LevelInformation getSelectedLevel() { return null; }
	@Override public void setSelectedLevel(LevelInformation difficulty) {}
	
	@Override public GameCharacter getSelectedCharacter() { return null; }
	@Override public void setSelectedCharacter(GameCharacter character) {}
	
	@Override public void tick(GameInput input) {}
}
