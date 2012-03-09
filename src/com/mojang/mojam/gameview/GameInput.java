package com.mojang.mojam.gameview;

import com.mojang.mojam.Keys;
import com.mojang.mojam.MouseButtons;

public interface GameInput {
	/*
	 * Advances the current key and mouse states to 
	 * that gathered since the last gatherInput(), 
	 * new input event are captured awaiting the
	 * next advance.
	 */
	public void gatherInput();
	/* 
	 * Returns the current game keys state
	 * Note: the menu system does not use this
	 *       for key input
	 */
	public Keys getKeys();
	/*
	 * Returns the current mouse buttons state. 
	 */
	public MouseButtons getMouseButtons();
	/* 
	 * Returns whether the mouse has moved
	 * since the last gatherInput()
	 */
	public boolean getMouseMoved();
	/*
	 * Registers the MenuInputHandler for the
	 * menu system as a KeyListener to the GameInput
	 */
	public void registerMenuInputHandler(MenuInputHandler menuInputHandler);
	/*
	 * Returns the logical key to physical key
	 * mapping.
	 */
	public KeyInputHandler getInputHandler(); 
}
