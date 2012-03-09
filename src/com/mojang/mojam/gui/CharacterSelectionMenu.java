package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.Options;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class CharacterSelectionMenu extends GuiMenu {

	private CharacterButton lordLard;
	private CharacterButton herrSpeck;
	private CharacterButton duchessDonut;
	private CharacterButton countessCruller;

	private CharacterButton selected;

	private Button focus;

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
		selected = lordLard = (CharacterButton) addButton(new CharacterButton(
				GameCharacter.LordLard, Art.getPlayer(GameCharacter.LordLard)[0][6],
				xOffset, yOffset));
		selected.setSelected(true);
		herrSpeck = (CharacterButton) addButton(new CharacterButton(
				GameCharacter.HerrVonSpeck, Art.getPlayer(GameCharacter.HerrVonSpeck)[0][2], xOffset + 20
						+ CharacterButton.WIDTH, yOffset));
		duchessDonut = (CharacterButton) addButton(new CharacterButton(
				GameCharacter.DuchessDonut,
				Art.getPlayer(GameCharacter.DuchessDonut)[0][6], xOffset, yOffset + 20
						+ CharacterButton.HEIGHT));
		countessCruller = (CharacterButton) addButton(new CharacterButton(
				GameCharacter.CountessCruller,
				Art.getPlayer(GameCharacter.CountessCruller)[0][2], xOffset + 20 + CharacterButton.WIDTH,
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
		focus = select = (Button) addButton(new Button("character.select", 
													   (GameView.WIDTH - 128) / 2, 
													   yOffset + 2 * CharacterButton.HEIGHT + 20 + 30));
		select.addListener(menus.BACK_BUTTON_LISTENER);
		
		back = (Button) addButton(new Button("back", (GameView.WIDTH - 128) / 2, 
											 yOffset + 2 * CharacterButton.HEIGHT + 20 + 60));
		back.addListener(menus.BACK_BUTTON_LISTENER);
	}

	@Override
	public void tick(MouseButtons mouseButtons) {
		walkTime++;
		super.tick(mouseButtons);
	}
	
	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("character.text"),
				screen.w / 2, yOffset - 24, Font.Align.CENTERED);
		if (focus == back || focus == select) {
			int frame = (walkTime / 4 % 6 + 6) % 6;
			screen.blit(Art.getPlayer(selected.getCharacter())[frame][(walkTime / 32) % 8],
					screen.w / 2 - 16, screen.h / 2 - 35);
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
			logic().setSelectedCharacter(selected.getCharacter());
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			if (focus == herrSpeck) {
				herrSpeck.setFocus(false);
				lordLard.setFocus(true);
				focus = lordLard;
			} else if (focus == countessCruller) {
				countessCruller.setFocus(false);
				duchessDonut.setFocus(true);
				focus = duchessDonut;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			if (focus == lordLard) {
				lordLard.setFocus(false);
				herrSpeck.setFocus(true);
				focus = herrSpeck;
			} else if (focus == duchessDonut) {
				duchessDonut.setFocus(false);
				countessCruller.setFocus(true);
				focus = countessCruller;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			if (focus == back) {
				focus = select;
			} else if (focus == select) {
				duchessDonut.setFocus(true);
				focus = duchessDonut;
			} else if (focus == duchessDonut) {
				duchessDonut.setFocus(false);
				lordLard.setFocus(true);
				focus = lordLard;
			} else if (focus == countessCruller) {
				countessCruller.setFocus(false);
				herrSpeck.setFocus(true);
				focus = herrSpeck;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			if (focus == select) {
				focus = back;
			} else if (focus == duchessDonut) {
				duchessDonut.setFocus(false);
				focus = select;
			} else if (focus == countessCruller) {
				countessCruller.setFocus(false);
				focus = select;
			} else if (focus == lordLard) {
				lordLard.setFocus(false);
				duchessDonut.setFocus(true);
				focus = duchessDonut;
			} else if (focus == herrSpeck) {
				herrSpeck.setFocus(false);
				countessCruller.setFocus(true);
				focus = countessCruller;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			focus.postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			back.postClick();		
		} else {
			super.keyPressed(e);
		}
	}
}
