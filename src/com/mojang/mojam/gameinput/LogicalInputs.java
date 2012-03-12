package com.mojang.mojam.gameinput;

import java.util.HashMap;
import java.util.Map;

/*
 * This class defines all the gameplay inputs used by the game
 * These can be bound to keys and buttons of input devices (keyboard, mouse, etc)
 */
public class LogicalInputs {
	/*
	 * This class defines an individual input and its state
	 * These represent an individual key or button on an input device (keyboard, mouse, etc)
	 */
	public final class LogicalInput {
		public final String name;
		public boolean wasPressed = false;
		public boolean wasReleased = false;
		public boolean isDown = false;

		public LogicalInput(String name) {
			this.name = name;
			if (all.containsKey(name)) throw new RuntimeException("Duplicate logical input name");
			all.put(name, this);
		}
		
		void copyInto(LogicalInput other) {
			other.wasPressed = wasPressed;
			other.wasReleased = wasReleased;
			other.isDown = isDown;
		}
	}

	Map<String, LogicalInput> all = new HashMap<String, LogicalInput>();

	public LogicalInput up = new LogicalInput("up");
	public LogicalInput down = new LogicalInput("down");
	public LogicalInput left = new LogicalInput("left");
	public LogicalInput right = new LogicalInput("right");
	public LogicalInput fire = new LogicalInput("fire");
    public LogicalInput fireUp = new LogicalInput("fireUp");
    public LogicalInput fireDown = new LogicalInput("fireDown");
    public LogicalInput fireLeft = new LogicalInput("fireLeft");
    public LogicalInput fireRight = new LogicalInput("fireRight");
	public LogicalInput build = new LogicalInput("build");
	public LogicalInput use = new LogicalInput("use");
	public LogicalInput upgrade = new LogicalInput("upgrade");
	public LogicalInput pause = new LogicalInput("pause");
	public LogicalInput fullscreen = new LogicalInput("fullscreen");
	public LogicalInput sprint = new LogicalInput("sprint");
	public LogicalInput screenShot = new LogicalInput("screenShot");
	public LogicalInput chat = new LogicalInput("chat");
	public LogicalInput console = new LogicalInput("console");
	
	public LogicalInput getLogicalInputByName(String name) {
		return all.get(name);
	}
	
	void copyInto(LogicalInputs other) {
		for (LogicalInput input : all.values()) {
			input.copyInto(other.getLogicalInputByName(input.name));
		}
	}
	
	void reset() {
		for (LogicalInput input : all.values()) {
			input.wasPressed = false;
			input.wasReleased = false;
		}
	}
}
