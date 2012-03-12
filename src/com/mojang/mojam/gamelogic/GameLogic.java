package com.mojang.mojam.gamelogic;

import com.mojang.mojam.GameInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.level.Level;

public interface GameLogic {
	public GameInformation getGameInformation();

	public Level getLevel();

	public Player[] getPlayers();
	public Player getLocalPlayer();
	
	public boolean isMouseActive();
	
	public void tick(GameInput input);
}
