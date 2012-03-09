package com.mojang.mojam.gameview;

public interface GameView {
	public static final int WIDTH = 512;
	public static final int HEIGHT = WIDTH * 3 / 4;
	public static final int SCALE = 2;

	public void renderView(GameInput input);
}
