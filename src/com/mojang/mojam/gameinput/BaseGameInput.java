package com.mojang.mojam.gameinput;

public abstract class BaseGameInput implements GameInput {
	LogicalInputs current = new LogicalInputs();
	LogicalInputs next = new LogicalInputs();
	
	@Override
	public void tick() {
		/* Advance current state */
		next.copyInto(current);
		/* Reset any per tick flags */
		next.reset();
	}

	@Override
	public LogicalInputs getCurrentState() {
		return current;
	}
}
