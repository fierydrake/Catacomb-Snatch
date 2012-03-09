package com.mojang.mojam.gamelogic;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.Level;

public class PendingGameLogic implements GameLogic {
	private DifficultyInformation difficulty;
	
	@Override public Level getLevel() { return null; }

	@Override public Player[] getPlayers() { return null; }
	@Override public Player getLocalPlayer() { return null; }
	
	@Override public DifficultyInformation getDifficulty() { return difficulty; }
	@Override public void setDifficulty(DifficultyInformation difficulty) { this.difficulty = difficulty; }
	
	@Override public void tick(GameInput input) {}
}
