package com.mojang.mojam;

import java.util.ArrayDeque;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gui.menus.GuiMenu;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class GameMenus {
	private ArrayDeque<GuiMenu> stack = new ArrayDeque<GuiMenu>();
	
	public boolean isShowing() { return !stack.isEmpty(); }
	public GuiMenu getCurrent() { return stack.peek(); }
	public void push(GuiMenu menu) { stack.push(menu); }
	public GuiMenu pop() { return stack.pop(); }
	public void clear() { stack.clear(); }
	
	private LocalGameInput localInput;
	
	public GameMenus(LocalGameInput localInput) {
		this.localInput = localInput;
	}
	
	public LocalGameInput getLocalInput() { return localInput; }
	
	public void tick() {
		getCurrent().tick(localInput);
	}
	
	public void render(Screen screen) {
		if (CatacombSnatch.isPlayingGame()) {
			screen.alphaFill(0, 0, screen.w, screen.h, 0xff000000, 0xC0);
		} else {
			screen.blit(Art.background, 0, 0);
		}
		getCurrent().render(screen);
	}
}
