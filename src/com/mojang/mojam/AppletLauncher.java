package com.mojang.mojam;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;

import com.mojang.mojam.gameinput.LocalGameInput;

public class AppletLauncher extends Applet implements Launcher {
	private static final long serialVersionUID = 1L;

	private CatacombSnatchCanvas canvas;
	
	public void init() {
		Dimension preferredSize = CatacombSnatch.getRendererSize();
		int scale = CatacombSnatch.getRendererScale();
		
		canvas = new CatacombSnatchCanvas(preferredSize, scale);
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		
	}
	
	@Override public void toggleFullscreen() {}
	@Override public void setFullscreen(boolean fullscreen) {}
	@Override public void setRendererSize(Dimension size) {}
	@Override public void setRendererScale(int scale) {}

	public void start() {
		CatacombSnatch.start(this, new LocalGameInput(canvas, canvas.getScale()), (ScreenRenderer)canvas);
	}

	public void stop() {
		CatacombSnatch.stop();
	}
}