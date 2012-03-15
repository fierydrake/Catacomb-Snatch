package com.mojang.mojam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.gamemode.GameMode;
import com.mojang.mojam.level.gamemode.GameModeGoldRush;
import com.mojang.mojam.level.gamemode.GameModeVanilla;

/*
 * This class represents the metadata about
 * a game either about to start, or in progress
 */
public class GameInformation {
	public static enum Type { SINGLE_PLAYER, SYNCHED_NETWORK };
	
	@SuppressWarnings("serial")
	public static final List<DifficultyInformation> DIFFICULTIES = Collections.unmodifiableList(new ArrayList<DifficultyInformation>() {{
		add(new DifficultyInformation("diffselect.easy", .5f, .5f, 1.5f, .5f, 0));
		add(new DifficultyInformation("diffselect.normal", 1, 1, 1, 1, 1));
		add(new DifficultyInformation("diffselect.hard", 3, 3, .5f, 1.5f, 2));
		add(new DifficultyInformation("diffselect.nightmare", 6, 5, .25f, 2.5f, 3));		
	}});		
	public static final DifficultyInformation DEFAULT_DIFFICULTY = DIFFICULTIES.get(1);
	
	@SuppressWarnings("serial")
	public static final List<GameMode> GAME_MODES = Collections.unmodifiableList(new ArrayList<GameMode>() {{
		add(new GameModeVanilla());
		add(new GameModeGoldRush());
	}});
	public static final GameMode DEFAULT_GAME_MODE = GAME_MODES.get(0);
	
	public DifficultyInformation difficulty = DEFAULT_DIFFICULTY;
	public GameMode mode = DEFAULT_GAME_MODE;
	public Type type;
	public LevelInformation level;
	
	public List<PlayerInformation> players = new ArrayList<PlayerInformation>();
	
	public Level generateLevel() throws IOException {
		return mode.generateLevel(difficulty, level);
	}
}
