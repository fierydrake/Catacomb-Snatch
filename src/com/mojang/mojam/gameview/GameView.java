package com.mojang.mojam.gameview;

import com.mojang.mojam.GameMenus;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gamelogic.GameLogic;

public interface GameView {
	public static final int WIDTH = 512;
	public static final int HEIGHT = WIDTH * 3 / 4;
	public static final int SCALE = 2;

	public void renderView(LocalGameInput input, GameMenus menus, GameLogic logic);
}
