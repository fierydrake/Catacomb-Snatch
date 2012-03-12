package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class JoinGameMenu extends GuiMenu {

	private Button joinButton;
	private Button cancelButton;

	public JoinGameMenu() {
		super();

		joinButton = (Button) addButton(new Button("mp.join", 100, 180)); // TODO TitleMenu.PERFORM_JOIN_ID
		cancelButton = (Button) addButton(new Button("cancel", 250, 180)); // TODO TitleMenu.CANCEL_JOIN_ID
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("mp.enterIP"), 100, 100);
		Font.defaultFont().draw(screen, "TODO", 100, 120); // TODO TitleMenu.ip + "-"
	}

	@Override
	public void tick(LocalGameInput input) {
		PhysicalInputs inputs = input.getCurrentPhysicalState();
		// Start on Enter, Cancel on Escape
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ENTER, KeyEvent.VK_E)) {
			// TODO
//			if (TitleMenu.ip.length() > 0) {
//				joinButton.postClick();
//			}
//		} else if (inputs.wasKeyPressed(KeyEvent.VK_BACK_SPACE) && TitleMenu.ip.length() > 0) {
//			TitleMenu.ip = TitleMenu.ip.substring(0, TitleMenu.ip.length() - 1);
//		} else {
//			TitleMenu.ip += e.getKeyChar();
		} else if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
			cancelButton.postClick();
		}
		super.tick(input);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
