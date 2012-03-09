package com.mojang.mojam;

import com.mojang.mojam.gamelogic.GameLogic;
import com.mojang.mojam.gamelogic.GameMenus;
import com.mojang.mojam.gamesound.GameSound;

public class SimpleGameElement {
	protected static final GameMenus menus = CatacombSnatch.menus;
	protected static final GameSound sound = CatacombSnatch.sound;
	protected GameLogic logic() { return CatacombSnatch.logic(); }
}
