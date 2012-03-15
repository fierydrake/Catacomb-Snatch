package com.mojang.mojam.gui.menus;

import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.screen.Art;
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
    	screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.FONT_RED.draw(screen, "ERROR", 15, 30);
		Font.defaultFont().draw(screen, message, 20, 40, 300);
	}
	
    @Override
	public void buttonPressed(ClickableComponent button) {
	}

}
