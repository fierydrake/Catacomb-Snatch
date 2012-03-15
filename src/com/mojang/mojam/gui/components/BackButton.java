package com.mojang.mojam.gui.components;

import static com.mojang.mojam.CatacombSnatch.menus;

public class BackButton extends Button {
	public BackButton(int x, int y) {
		super("back", x, y);
	}

	public BackButton(String staticTextsID, int x, int y) {
		super(staticTextsID, x, y);
	}

    @Override
	public void clicked() {
		menus().pop();
	}
}
