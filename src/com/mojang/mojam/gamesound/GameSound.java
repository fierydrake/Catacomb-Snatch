package com.mojang.mojam.gamesound;


public interface GameSound {
	public static final String BACKGROUND_TRACK = "background";
	public void startTitleMusic();
	public void startEndMusic();
	public void startBackgroundMusic();
	public void stopBackgroundMusic();
	public void setListenerPosition(float x, float y);
	public boolean playSound(String sourceName, float x, float y);
	public boolean playSound(String sourceName, float x, float y, boolean blocking);
	public void shutdown();
	public boolean isMuted();
	public void setMuted(boolean muted);
	public void setMasterVolume(float volume);
	public void setVolume(String sourceName, float volume);
}
