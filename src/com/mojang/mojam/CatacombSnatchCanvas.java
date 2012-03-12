package com.mojang.mojam;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.screen.Screen;

public class CatacombSnatchCanvas extends Canvas implements ScreenRenderer {
	private static final long serialVersionUID = 1L;
	
	public CatacombSnatchCanvas(int width, int height) {
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		Cursor emptyCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "empty");
		setCursor(emptyCursor);
		requestFocusInWindow();
	}

	/*
	 * ScreenRenderer
	 */
	public void render(GameView gameView, Screen screen, int scaledWidth, int scaledHeight) {
		// Render
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			bs = getBufferStrategy();
		}
		Graphics g = bs.getDrawGraphics();
		
		// Clear the graphics context
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.translate((getWidth() - scaledWidth) / 2, (getHeight() - scaledHeight) / 2);
		g.clipRect(0, 0, scaledWidth, scaledHeight);
		
		// Render screen
		g.drawImage(screen.image, 0, 0, scaledWidth, scaledHeight, null);
		
		try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
		bs.show();
	}
}