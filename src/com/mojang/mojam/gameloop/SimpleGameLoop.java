package com.mojang.mojam.gameloop;


/*
 * This class is a basic single-threaded implementation
 * of a GameLoop that will attempt to call each passed
 * Runnable at a constant rate
 */
public class SimpleGameLoop {
	private static final double ONE_SECOND_IN_NANOSECONDS = 1000000000;
	private double nsPerRun;
	private Runnable renderCallback;
	private Runnable logicCallback;

	public SimpleGameLoop(int runsPerSecond, Runnable renderCallback, Runnable logicCallback) {
		this.renderCallback = renderCallback;
		this.logicCallback = logicCallback;
		this.nsPerRun = ONE_SECOND_IN_NANOSECONDS / runsPerSecond;
	}
	
	public void start() {
		long timePassed = 0;
		long lastIterationTime = System.nanoTime();

		while (true) {
			long now = System.nanoTime();
			timePassed += now - lastIterationTime;
			lastIterationTime = now;

			boolean logicRan = false;
			while (timePassed >= nsPerRun) {
				logicCallback.run();
				timePassed -= nsPerRun;
				logicRan = true;
			}
			
			if (logicRan) renderCallback.run();
		}
	}
}
