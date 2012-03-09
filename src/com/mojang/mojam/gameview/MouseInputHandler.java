package com.mojang.mojam.gameview;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.mojang.mojam.MouseButtons;

public class MouseInputHandler implements MouseListener, MouseMotionListener {

	private MouseButtons mouseButtons;
	private boolean mouseMoved = false;

	public MouseInputHandler(MouseButtons mouseButtons) {
		this.mouseButtons = mouseButtons;
	}
	
	public boolean tickMouseMoved() {
		boolean result = mouseMoved;
		mouseMoved = false;
		return result;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved = true;
		mouseButtons.setPosition(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseMoved = true;
		mouseButtons.setPosition(e.getPoint());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseButtons.releaseAll();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseButtons.setNextState(e.getButton(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButtons.setNextState(e.getButton(), false);
	}
}
