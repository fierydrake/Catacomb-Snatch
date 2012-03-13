package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class LocaleMenu extends GuiMenu {
	public static final String[] LOCALES = { 
		"en", "de", "es", "fr", "ind", "sv", "it", "nl", "pt_br", "ru", "sl", "af"
	};
	private final Button[] LOCALE_BUTTONS = new Button[LOCALES.length]; 

	private int textY;
	private int numRows;
	private Button back;

	public LocaleMenu() {
		super();
		
		int offset = 32;
		int left_xOffset = (int) ((GameView.WIDTH / 2) - (Button.WIDTH * 1.2));
		int right_xOffset = (int) ((GameView.WIDTH / 2) + (Button.WIDTH * 1.2 - (Button.WIDTH)));
		int xOffset = (GameView.WIDTH - Button.WIDTH) / 2;
		int yOffset = (GameView.HEIGHT - (7 * offset + 20 + (offset * 2))) / 2;
		textY = yOffset;
		yOffset += offset;
		numRows = (LOCALE_BUTTONS.length + 1) / 2; /* odd numbers will round up */
		int TopYOffset = yOffset;
		
		for (int i=0; i<numRows; i++) { 
			LOCALE_BUTTONS[i] = new Button(Constants.getString("options.locale_" + LOCALES[i]), left_xOffset, (yOffset += offset), false);
			addButton(LOCALE_BUTTONS[i]);
		}
		yOffset = TopYOffset;
		for (int i=numRows; i<LOCALE_BUTTONS.length; i++) { 
			LOCALE_BUTTONS[i] = new Button(Constants.getString("options.locale_" + LOCALES[i]), right_xOffset, (yOffset += offset), false);
			addButton(LOCALE_BUTTONS[i]);
		}
		yOffset += offset;
		back = (Button) addButton(new BackButton(xOffset, (yOffset += offset) + 20));
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		for (int i=0; i<LOCALE_BUTTONS.length; i++) {
			if (button == LOCALE_BUTTONS[i]) {
				Texts.setLocale(LOCALES[i]);
				break;
			}
		}
	}

	@Override
	public void tick(LocalGameInput input) {
		PhysicalInputs inputs = input.getCurrentPhysicalState();
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
			back.postClick();
		}
		if (focusedItem + 1 <= numRows) {
			if (inputs.wasKeyPressedConsume(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
				focusedItem = Mth.clamp(focusedItem + numRows, 0, LOCALE_BUTTONS.length - 1);
			}
		}
		if (focusedItem + 1 > numRows && focusedItem < LOCALE_BUTTONS.length) {
			if (inputs.wasKeyPressedConsume(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
				focusedItem -= numRows;
			}
		}
		super.tick(input);
	}
	
	@Override
	public void render(Screen screen) {
		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("options.locale_selection"), screen.w / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(focusedItem).getX() - 40, buttons.get(focusedItem).getY() - 6);
	}
}
