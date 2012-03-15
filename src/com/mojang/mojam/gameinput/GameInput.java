package com.mojang.mojam.gameinput;

import java.awt.Point;

public interface GameInput {
	/*
	 * Advances the current key and mouse states to 
	 * that gathered since the last gatherInput(), 
	 * new input event are captured awaiting the
	 * next advance.
	 */
	public void gatherInput();

	public LogicalInputs getCurrentState();
	public boolean isMouseActive();
	public Point getMousePosition();
}
