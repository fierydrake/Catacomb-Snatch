package com.mojang.mojam;

import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.screen.Screen;

public interface ScreenRenderer {
	public void render(GameView gameView, Screen screen, int scaledWidth, int scaledHeight);
}
