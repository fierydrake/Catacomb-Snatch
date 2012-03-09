package com.mojang.mojam.gamelogic;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.Level;

public interface GameLogic {
	public Level getLevel();

	public Player[] getPlayers();
	public Player getLocalPlayer();
	
	public DifficultyInformation getDifficulty();
	public void setDifficulty(DifficultyInformation difficulty);
	
	public void tick(GameInput input);
}
