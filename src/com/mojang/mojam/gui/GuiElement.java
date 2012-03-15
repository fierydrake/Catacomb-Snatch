package com.mojang.mojam.gui;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.screen.Screen;

public abstract class GuiElement {

	public void render(Screen screen) {
	}
	
	public void tick(LocalGameInput input) {
	}

}
