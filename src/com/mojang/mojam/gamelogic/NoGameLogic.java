package com.mojang.mojam.gamelogic;

import com.mojang.mojam.GameInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.level.Level;

public class NoGameLogic implements GameLogic {
	@Override public GameInformation getGameInformation() { return null; }
	
	@Override public Level getLevel() { return null; }

	@Override public Player[] getPlayers() { return null; }
	@Override public Player getLocalPlayer() { return null; }
	
	@Override public boolean isMouseActive() { return false; }
	
	@Override public void tick(GameInput input) {}
}
