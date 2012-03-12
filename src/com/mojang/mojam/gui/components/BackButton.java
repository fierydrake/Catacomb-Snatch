package com.mojang.mojam.gui.components;


public class BackButton extends Button {
	public BackButton(int x, int y) {
		super("back", x, y);
	}

	public BackButton(String staticTextsID, int x, int y) {
		super(staticTextsID, x, y);
	}

	public void clicked() {
		menus.pop();
	}
}
