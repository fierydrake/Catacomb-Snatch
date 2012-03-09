package com.mojang.mojam.gameview;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.mojang.mojam.gamelogic.GameMenus;

public class MenuInputHandler implements KeyListener {
	private GameMenus menus;
	
	public MenuInputHandler(GameMenus menus) {
		this.menus = menus;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (menus.isShowing()) {
			menus.getCurrent().keyPressed(e);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (menus.isShowing()) {
			menus.getCurrent().keyReleased(e);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (menus.isShowing()) {
			menus.getCurrent().keyTyped(e);
		}
	}
}
