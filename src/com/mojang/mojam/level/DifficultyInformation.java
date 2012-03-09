package com.mojang.mojam.level;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.gamelogic.GameLogic;

public class DifficultyInformation {
	private static GameLogic logic() { return CatacombSnatch.logic(); }
	
	public String difficultyName;
	public int difficultyID;

	public final float mobHealthModifier;
	public final float mobStrengthModifier;
	public final float mobSpawnModifier;
	public final float shopCostsModifier;

	public DifficultyInformation(String difficultyName, float mobHealthModifier, float mobStrengthModifier, float mobSpawnModifier, float shopCostsModifier, int difficultyID) {
		this.difficultyName = difficultyName;
		this.mobHealthModifier = mobHealthModifier;
		this.mobStrengthModifier = mobStrengthModifier;
		this.mobSpawnModifier = mobSpawnModifier;
		this.shopCostsModifier = shopCostsModifier;
		this.difficultyID = difficultyID;
	}

	public static float calculateHealth(float baseHealth) {
		if(logic().getDifficulty() != null)
			return baseHealth * logic().getDifficulty().mobHealthModifier;
		else
			return 0;
	}

	public static float calculateStrength(int baseStrength) {
		if(logic().getDifficulty() != null)
			return baseStrength * logic().getDifficulty().mobStrengthModifier;
		else
			return 0;
	}

	public static int calculateSpawntime(int baseSpawntime) {
		if(logic().getDifficulty() != null)
			return (int)(baseSpawntime * logic().getDifficulty().mobSpawnModifier);
		else
			return 0;
	}

	public static int calculateCosts(int baseCost) {
		if(logic().getDifficulty() != null)
			return (int)(baseCost * logic().getDifficulty().shopCostsModifier);
		else
			return 0;
	}
}
