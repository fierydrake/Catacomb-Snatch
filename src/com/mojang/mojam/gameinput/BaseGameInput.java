package com.mojang.mojam.gameinput;

public abstract class BaseGameInput implements GameInput {
	LogicalInputs current = new LogicalInputs();
	LogicalInputs next = new LogicalInputs();
	
	@Override
	public synchronized void gatherInput() {
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
