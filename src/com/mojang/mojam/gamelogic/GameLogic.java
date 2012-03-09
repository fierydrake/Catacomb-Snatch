package com.mojang.mojam.gamelogic;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.LevelInformation;

public interface GameLogic {
	public boolean isPlayingLevel();

	public Player[] getPlayers();
	public Player getLocalPlayer();
	
	public DifficultyInformation getSelectedDifficulty();
	public void setSelectedDifficulty(DifficultyInformation difficulty);

	public LevelInformation getSelectedLevel();
	public void setSelectedLevel(LevelInformation difficulty);

	public GameCharacter getSelectedCharacter();
	public void setSelectedCharacter(GameCharacter character);
	
	public void tick(GameInput input);
}
