package com.mojang.mojam.gui;

import com.mojang.mojam.screen.Screen;

public class GuiError extends GuiMenu {

	String message;
	
	public GuiError(String message){
		super();
		
		this.message = message;
		addButton(new Button("error.mainmenu", 125, 300));
	}
	
	@Override
	public void render(Screen screen) {
		screen.clear(0);
		Font.FONT_RED.draw(screen, "ERROR", 15, 30);
		Font.defaultFont().draw(screen, message, 20, 40, 300);
		super.render(screen);
	}
	
	public void buttonPressed(ClickableComponent button) {
	}

}
