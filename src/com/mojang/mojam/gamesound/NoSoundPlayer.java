package com.mojang.mojam.gamesound;

public class NoSoundPlayer implements GameSound {

	@Override
	public void startTitleMusic() {
		// Dummy method; no implementation needed
	}

	@Override
	public void startEndMusic() {
		// Dummy method; no implementation needed
	}

	@Override
	public void startBackgroundMusic() {
		// Dummy method; no implementation needed
	}

	@Override
	public void stopBackgroundMusic() {
		// Dummy method; no implementation needed
	}

	@Override
	public void setListenerPosition(float x, float y) {
		// Dummy method; no implementation needed
	}

	@Override
	public boolean playSound(String sourceName, float x, float y) {
		// Dummy method; no implementation needed
		return false;
	}

	@Override
	public boolean playSound(String sourceName, float x, float y, boolean blocking) {
		// Dummy method; no implementation needed
		return false;
	}

	@Override
	public void shutdown() {
		// Dummy method; no implementation needed
	}

	@Override
	public boolean isMuted() {
		// Dummy method; no implementation needed
		return false;
	}

	@Override
	public void setMuted(boolean muted) {
		// Dummy method; no implementation needed
	}

	@Override
	public void setMasterVolume(float volume) {
		// Dummy method; no implementation needed		
	}

	@Override
	public void setVolume(String sourceName, float volume) {
		// Dummy method; no implementation needed
	}

}
