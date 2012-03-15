package com.mojang.mojam.gamelogic;

import java.util.List;

import com.mojang.mojam.GameInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Screen;

public interface GameLogic {
	public boolean isPlaying();
	
	public GameInformation getGame();

	public Level getLevel();

	public List<Player> getPlayers();
	
	public void tick();
	public void renderViews(Screen screen);
}
