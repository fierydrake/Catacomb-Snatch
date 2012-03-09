package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.SimpleGameElement;
import com.mojang.mojam.level.Level;

public class GoldRush extends SimpleGameElement implements IVictoryConditions {
	
	private boolean bVictoryAchieved;
	private int winner;
	
	@Override
	public void updateVictoryConditions(Level level) {
		if (logic().getPlayers()[0] != null)
			level.player1Score = logic().getPlayers()[0].score;
		if (logic().getPlayers()[1] != null)
			level.player2Score = logic().getPlayers()[1].score;
		
		if (level.player1Score >= level.TARGET_SCORE) {
        	bVictoryAchieved = true;
        	winner = 1;
        }
        if (level.player2Score >= level.TARGET_SCORE) {
        	bVictoryAchieved = true;
        	winner = 2;
        }
	}
	
	@Override
	public boolean isVictoryConditionAchieved() {
		return bVictoryAchieved;
	}
	
	@Override
	public int playerVictorious() {
		return winner;
	}
}
