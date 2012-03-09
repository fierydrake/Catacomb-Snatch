package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.Keys;
import com.mojang.mojam.Keys.Key;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class KeyBindingsMenu extends GuiMenu {

	class KeyBindingButton extends Button {
		private Key key;
		private boolean selected = false;

		public KeyBindingButton(Key key, int x, int y) {
			super(getMenuText(key), x, y, false);
			this.key = key;
		}
		
		@Override
		public String labelText() {
			return getMenuText(key);
		}

		public Key getKey() {
			return key;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		public void refresh() {
			updateLabel();
		}
		
		@Override
		protected void blitBackground(Screen screen, int bitmapId) {
			super.blitBackground(screen, selected ? 1 : bitmapId);
		}
	}

	private static final int BORDER = 10;
	private static final int BUTTON_SPACING = 32;

	private int textWidth;
	private int yOffset;

	private ClickableComponent back;
	private KeyBindingButton selectedKey = null;

	public KeyBindingsMenu() {
		super();
		addButtons();
	}

	private void addButtons() {
		textWidth = (GameView.WIDTH - 2 * BORDER - 2 * 32 - 2 * Button.WIDTH) / 2;
		int numRows = 6;
		int tab1 = BORDER + 32 + textWidth;
		int tab2 = GameView.WIDTH - BORDER - Button.WIDTH;
		yOffset = (GameView.HEIGHT - (numRows * BUTTON_SPACING + 32)) / 2;

		Keys keys = CatacombSnatch.input.getKeys();
		addButton(new KeyBindingButton(keys.up, tab1, yOffset + 0 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.down, tab1, yOffset + 1 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.left, tab1, yOffset + 2 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.right, tab1, yOffset + 3 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.sprint, tab1, yOffset + 4 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.fire, tab1, yOffset + 5 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.console, tab1, yOffset + 6 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.fireUp, tab2, yOffset + 0 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.fireDown, tab2, yOffset + 1 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.fireLeft, tab2, yOffset + 2 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.fireRight, tab2, yOffset + 3 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.build, tab2, yOffset + 4 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.use, tab2, yOffset + 5 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.upgrade, tab2, yOffset + 6 * BUTTON_SPACING));
		addButton(new KeyBindingButton(keys.chat, tab2, yOffset + 7	* BUTTON_SPACING));
		back = addButton(new Button("back",	(GameView.WIDTH - Button.WIDTH) / 2, 
				yOffset + numRows * BUTTON_SPACING - Button.HEIGHT + 88));
		back.addListener(menus.BACK_BUTTON_LISTENER);
	}

	private String getMenuText(Key key) {
		Integer keyEvent = CatacombSnatch.input.getInputHandler().getKeyEvent(key);
		if (keyEvent != null && keyEvent != KeyEvent.VK_UNDEFINED) {
			return KeyEvent.getKeyText(keyEvent);
		}
		// TODO put text in translation file
		return "NONE";
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		Texts txts = Texts.current();
		Font.defaultFont().draw(screen, txts.getStatic("options.keyBindings"), screen.w / 2, yOffset - 40, Font.Align.CENTERED);
		write(screen, txts.getStatic("keys.up"), 0, 0);
		write(screen, txts.getStatic("keys.down"), 0, 1);
		write(screen, txts.getStatic("keys.left"), 0, 2);
		write(screen, txts.getStatic("keys.right"), 0, 3);
		write(screen, txts.getStatic("keys.sprint"), 0, 4);
		write(screen, txts.getStatic("keys.fire"), 0, 5);
		write(screen, "CONSOLE", 0, 6); //add translations

		write(screen, txts.getStatic("keys.fireUp"), 1, 0);
		write(screen, txts.getStatic("keys.fireDown"), 1, 1);
		write(screen, txts.getStatic("keys.fireLeft"), 1, 2);
		write(screen, txts.getStatic("keys.fireRight"), 1, 3);
		write(screen, txts.getStatic("keys.build"), 1, 4);
		write(screen, txts.getStatic("keys.use"), 1, 5);
		write(screen, txts.getStatic("keys.upgrade"), 1, 6);
		write(screen, txts.getStatic("keys.chat"), 1, 7);
		super.render(screen);
		ClickableComponent button = buttons.get(selectedItem);
		if (button == back) {
			screen.blit(Art.getLocalPlayerArt()[0][6], back.getX() - 64, back.getY() - 8);
		} else {
			screen.blit(Art.getLocalPlayerArt()[0][6], button.getX() - textWidth - 32,
					button.getY() - 8);
		}
	}

	private void write(Screen screen, String txt, int column, int row) {
		Font.defaultFont().draw(screen, txt + ": ", BORDER + 32 + textWidth + column
				* (Button.WIDTH + 32 + textWidth), yOffset
				+ 8 + row * BUTTON_SPACING, Font.Align.RIGHT);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		boolean swapped = selectedKey == button;
		if (selectedKey != null) {
			selectedKey.setSelected(false);
			selectedKey = null;
		}
		if (button == back || swapped) {
			return;
		}
		selectedKey = (KeyBindingButton) button;
		selectedKey.setSelected(true);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (selectedKey != null) {
			CatacombSnatch.input.getInputHandler().addMapping(selectedKey.getKey(), e.getKeyCode());
			selectedKey.setSelected(false);
			selectedKey = null;
			refreshKeys();	
		} else {			
			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
				if (buttons.get(selectedItem) == back) {
					selectedItem -= 6;
				} else if (selectedItem >= 5) {
					selectedItem -= 5;
				}
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
				if (selectedItem < 5) {
					selectedItem += 5;
				} else if (buttons.get(selectedItem) == back) {
					selectedItem--;
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				back.postClick();
			} else {
				super.keyPressed(e);
			}
		}
	}
	
	public void refreshKeys() {
		for(ClickableComponent button : super.buttons) {
			if(button instanceof KeyBindingButton)
				((KeyBindingButton)button).refresh();
		}
	}
}
