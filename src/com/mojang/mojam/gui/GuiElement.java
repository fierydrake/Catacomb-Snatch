package com.mojang.mojam.gui;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameMenus;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gamelogic.GameLogic;
import com.mojang.mojam.gamesound.GameSound;
import com.mojang.mojam.screen.Screen;

public abstract class GuiElement {
	protected final GameMenus menus = CatacombSnatch.menus;
	protected final GameSound sound = CatacombSnatch.sound;
	protected GameLogic logic() { return menus.getGameLogic(); }

	public void render(Screen screen) {
	}
	
	public void tick(LocalGameInput input) {
	}

}
