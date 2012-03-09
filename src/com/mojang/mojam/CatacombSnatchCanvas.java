package com.mojang.mojam;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gameview.KeyInputHandler;
import com.mojang.mojam.gameview.MenuInputHandler;
import com.mojang.mojam.gameview.MouseInputHandler;
import com.mojang.mojam.screen.Screen;

public class CatacombSnatchCanvas extends Canvas implements ScreenRenderer, GameInput {
	private static final long serialVersionUID = 1L;
	
	public CatacombSnatchCanvas(int width, int height) {
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
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

	/*
	 * GameInput
	 */
	private MouseButtons mouseButtons = new MouseButtons();
	private Keys keys = new Keys();
	private KeyInputHandler keyInputHandler = new KeyInputHandler(keys);
	private MouseInputHandler mouseInputHandler = new MouseInputHandler(mouseButtons);
	private boolean mouseMoved = false;
	private boolean listenersRegistered = false;
	
	private void ensureListenersRegistered() {
		if (!listenersRegistered) {
			addKeyListener(keyInputHandler);
			addMouseMotionListener(mouseInputHandler);
			addMouseListener(mouseInputHandler);
			listenersRegistered = true;
		}
	}

	@Override
	public void registerMenuInputHandler(MenuInputHandler menuInputHandler) {
		addKeyListener(menuInputHandler);
	}
	
	@Override
	public void gatherInput() {
		ensureListenersRegistered();
		keys.tick();
		mouseButtons.tick();
		mouseMoved = mouseInputHandler.tickMouseMoved();
	}

	@Override
	public Keys getKeys() {
		return keys;
	}

	@Override
	public MouseButtons getMouseButtons() {
		return mouseButtons;
	}

	@Override
	public boolean getMouseMoved() {
		return mouseMoved;
	}

	@Override
	public KeyInputHandler getInputHandler() {
		return keyInputHandler;
	}
}
