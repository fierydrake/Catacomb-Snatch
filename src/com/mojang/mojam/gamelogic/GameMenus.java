package com.mojang.mojam.gamelogic;

import java.util.ArrayDeque;

import com.mojang.mojam.gameview.GameInput;
import com.mojang.mojam.gui.ButtonAdapter;
import com.mojang.mojam.gui.ButtonListener;
import com.mojang.mojam.gui.ClickableComponent;
import com.mojang.mojam.gui.GuiMenu;

public class GameMenus {
	private ArrayDeque<GuiMenu> stack = new ArrayDeque<GuiMenu>();
	
	public class BackButtonAdapter extends ButtonAdapter {
		@Override
		public void buttonPressed(ClickableComponent button) {
			GameMenus.this.pop();
		}
	}
	
	public final ButtonListener BACK_BUTTON_LISTENER = new BackButtonAdapter();
	
	public boolean isShowing() { return !stack.isEmpty(); }
	public GuiMenu getCurrent() { return stack.peek(); }
	public void push(GuiMenu menu) { stack.push(menu); }
	public GuiMenu pop() { return stack.pop(); }
	
	public void tick(GameInput input) {
		getCurrent().tick(input.getMouseButtons()); // FIXME should not need to pass this
	}
}
