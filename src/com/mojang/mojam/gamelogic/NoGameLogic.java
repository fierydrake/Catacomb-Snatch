package com.mojang.mojam.gamelogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.mojam.GameInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Screen;

public class NoGameLogic implements GameLogic {
	/* Keep an empty list here to save us creating empty lists on-the-fly */
	private final List<Player> players = Collections.unmodifiableList(new ArrayList<Player>());
	
	@Override public boolean isPlaying() { return false; }
	@Override public GameInformation getGame() { return null; }
	@Override public Level getLevel() { return null; }
	@Override public List<Player> getPlayers() { return players; }
	@Override public void tick() {}
	@Override public void renderViews(Screen screen) {}
}
