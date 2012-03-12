package com.mojang.mojam.level.gamemode;

import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.level.Level;

public class FullTreasury implements IVictoryConditions {
	
	private boolean bVictoryAchieved;
	private Team winner;
	@Override
	public void updateVictoryConditions(Level level) {
		if (level != null) {
            if (level.player1Score >= level.TARGET_SCORE) {
            	bVictoryAchieved = true;
            	winner = Team.Team1;
            }
            if (level.player2Score >= level.TARGET_SCORE) {
            	bVictoryAchieved = true;
            	winner = Team.Team2;
            }
        }
	}

	@Override
	public boolean isVictoryConditionAchieved() {
		return bVictoryAchieved;
	}

	@Override
	public Team playerVictorious() {
		return winner;
	}

}
