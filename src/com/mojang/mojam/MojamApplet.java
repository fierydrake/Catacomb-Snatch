package com.mojang.mojam;

import java.applet.Applet;
import java.awt.BorderLayout;

public class MojamApplet extends Applet {
	private static final long serialVersionUID = 1L;

	public void init() {
		setLayout(new BorderLayout());
		add(CatacombSnatch.canvas, BorderLayout.CENTER);
	}

	public void start() {
		CatacombSnatch.start();
	}

	public void stop() {
		CatacombSnatch.stop();
	}
}