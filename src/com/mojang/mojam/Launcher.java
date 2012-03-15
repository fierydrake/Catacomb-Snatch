package com.mojang.mojam;

import java.awt.Dimension;

public interface Launcher {
	public void toggleFullscreen();
	public void setFullscreen(boolean fullscreen);
	public void setRendererSize(Dimension size);
	public void setRendererScale(int scale);
}
