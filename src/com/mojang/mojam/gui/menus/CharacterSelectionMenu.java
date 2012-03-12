package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.Options;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.CharacterButton;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class CharacterSelectionMenu extends GuiMenu {

	private CharacterButton selected;

	private Button select;
	private Button back;

	private int xOffset, yOffset;
	private int walkTime;

	public CharacterSelectionMenu() {
		super();
		
		addButtons();
	}

	private void addButtons() {
		xOffset = (GameView.WIDTH - (CharacterButton.WIDTH * 2 + 20)) / 2;
		yOffset = (GameView.HEIGHT - (CharacterButton.HEIGHT * 2 + 20)) / 2 - 20;
		
		selected = new CharacterButton(GameCharacter.LordLard, 
									   Art.getPlayer(GameCharacter.LordLard)[0][6],
									   xOffset, yOffset);
		selected.setSelected(true);
		addButton(selected);
		
		addButton(new CharacterButton(GameCharacter.HerrVonSpeck, 
									  Art.getPlayer(GameCharacter.HerrVonSpeck)[0][2], 
									  xOffset + 20 + CharacterButton.WIDTH, yOffset));
		
		addButton(new CharacterButton(GameCharacter.DuchessDonut,
									  Art.getPlayer(GameCharacter.DuchessDonut)[0][6], 
									  xOffset, yOffset + 20 + CharacterButton.HEIGHT));
		
		addButton(new CharacterButton(GameCharacter.CountessCruller,
									  Art.getPlayer(GameCharacter.CountessCruller)[0][2], 
									  xOffset + 20 + CharacterButton.WIDTH,
									  yOffset + 20 + CharacterButton.HEIGHT));
		
		if (Options.isCharacterIDset()) {
			selected.setSelected(false);
			for (ClickableComponent button : buttons) {
				CharacterButton charButton = (CharacterButton) button;
				if (charButton.getCharacter().ordinal() == Options.getCharacterID()) {
					selected = charButton;
					break;
				}
			}
			selected.setSelected(true);
		}
		select = (Button) addButton(new BackButton("character.select", 
												   (GameView.WIDTH - 128) / 2, 
												   yOffset + 2 * CharacterButton.HEIGHT + 20 + 30));
		
		back = (Button) addButton(new BackButton((GameView.WIDTH - 128) / 2, 
											     yOffset + 2 * CharacterButton.HEIGHT + 20 + 60));
	}

	@Override
	public void tick(LocalGameInput input) {
		walkTime++;
		PhysicalInputs inputs = input.getCurrentPhysicalState();
		if (inputs.wasKeyPressed(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
			inputs.consumeKeyPresses(KeyEvent.VK_LEFT, KeyEvent.VK_A);
			if (focusedItem == 1 || focusedItem == 3) focusedItem--;
		}
		if (inputs.wasKeyPressed(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
			inputs.consumeKeyPresses(KeyEvent.VK_RIGHT, KeyEvent.VK_D);
			if (focusedItem == 0 || focusedItem == 2) focusedItem++;

		}
		if (inputs.wasKeyPressed(KeyEvent.VK_UP, KeyEvent.VK_W)) {
			if (focusedItem == 2 || focusedItem == 3) {
				inputs.consumeKeyPresses(KeyEvent.VK_UP, KeyEvent.VK_W);
				focusedItem -= 2;
			}
		}
		if (inputs.wasKeyPressed(KeyEvent.VK_DOWN, KeyEvent.VK_S)) {
			if (focusedItem == 2 || focusedItem == 3) {
				inputs.consumeKeyPresses(KeyEvent.VK_DOWN, KeyEvent.VK_S);
				focusedItem = 4;
			}
			if (focusedItem <= 2) {
				inputs.consumeKeyPresses(KeyEvent.VK_DOWN, KeyEvent.VK_S);
				focusedItem += 2;
			}
		}
		if (inputs.wasKeyPressed(KeyEvent.VK_ESCAPE)) {
			inputs.consumeKeyPresses(KeyEvent.VK_ESCAPE);
			back.postClick();
		}
		super.tick(input);
	}
	
	@Override
	public void render(Screen screen) {
    	screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().draw(screen, 
				                Texts.current().getStatic("character.text"), 
				                screen.w / 2, yOffset - 24, 
				                Font.Align.CENTERED);

		int frame = (walkTime / 4 % 6 + 6) % 6;
		screen.blit(Art.getPlayer(selected.getCharacter())[frame][(walkTime / 32) % 8], screen.w / 2 - 16, screen.h / 2 - 35);

		int x = buttons.get(focusedItem).getX() - 40;
		int y = buttons.get(focusedItem).getY() - 6;
		if (focusedItem == buttons.indexOf(select)) {
			screen.blit(Art.getPlayer(selected.getCharacter())[0][6], x, y);
		}
		if (focusedItem == buttons.indexOf(back)) {
			screen.blit(Art.getLocalPlayerArt()[0][6], x, y);
		}
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		if (button instanceof CharacterButton) {
			selected.setSelected(false);
			selected = (CharacterButton) button;
			selected.setSelected(true);
		} else if (button == select) {
			Options.set(Options.CHARACTER_ID, selected.getCharacter().ordinal());
			Options.saveProperties();
			CatacombSnatch.selectedCharacter = selected.getCharacter();
		}
	}
}

