package com.mojang.mojam;

import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.gameview.GameView;

public class PlayerInformation {
	public Team team;
	public GameCharacter character;
	public GameInput input;
	public GameView view;
	
	public PlayerInformation(Team team, GameCharacter character, GameInput input, GameView view) {
		this.team = team;
		this.character = character;
		this.input = input;
		this.view = view;
	}
}
