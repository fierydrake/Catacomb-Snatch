package com.mojang.mojam.level;


public class DifficultyInformation {
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

	public float calculateHealth(float baseHealth) {
		return baseHealth * mobHealthModifier;
	}

	public float calculateStrength(int baseStrength) {
		return baseStrength * mobStrengthModifier;
	}

	public int calculateSpawntime(int baseSpawntime) {
		return (int)(baseSpawntime * mobSpawnModifier);
	}

	public int calculateCosts(int baseCost) {
		return (int)(baseCost * shopCostsModifier);
	}
}
