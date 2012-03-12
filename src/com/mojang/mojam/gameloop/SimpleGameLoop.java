package com.mojang.mojam.gameloop;


/*
 * This class is a basic single-threaded implementation
 * of a GameLoop that will attempt to call each passed
 * Runnable at a constant rate
 */
public class SimpleGameLoop {
	private static final double ONE_SECOND_IN_NANOSECONDS = 1000000000;
	private static final int MAX_LOGIC_RUNS_PER_RENDER = 20;
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

			int logicRuns = 0;
			while (timePassed >= nsPerRun && logicRuns <= MAX_LOGIC_RUNS_PER_RENDER) {
				logicCallback.run();
				timePassed -= nsPerRun;
				logicRuns++;
			}
			
			/* if logic ran, then render */
			if (logicRuns > 0) renderCallback.run();
		}
	}
}
