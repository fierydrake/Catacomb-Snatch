package com.mojang.mojam.gameview;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.screen.Screen;

public interface GameView {
	public static final int WIDTH = 512;
	public static final int HEIGHT = WIDTH * 3 / 4;

	public int getViewportBoundsX();
	public int getViewportBoundsY();
	public int getViewportBoundsMX();
	public int getViewportBoundsMY();
	public int getViewportWidth();
	public int getViewportHeight();

	/*
	 * The GameView may be created without a player set,
	 * so this method will be called before the view is
	 * expected to render
	 * 
	 * The Player object should have everything the view
	 * needs to render the player's viewport onto the game
	 * (the Player state, the GameLogic (which in turn has
	 * the Level) and the current input state).
	 */
	public void setPlayer(Player player);
	
	public Player getPlayer();
	
	public void renderView(Screen screen);
}
