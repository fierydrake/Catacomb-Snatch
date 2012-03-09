package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.Options;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class OptionsMenu extends GuiMenu {

	private boolean creative;

	private int textY;
	
	private Button back;

	public OptionsMenu() {
		loadOptions();

		int offset = 32;
		int xOffset = (GameView.WIDTH- Button.WIDTH) / 2;
		int yOffset = (GameView.HEIGHT - (7 * offset + 20 + (offset * 2))) / 2;
		textY = yOffset;
		yOffset += offset;

		addButton(new Button("options.keyBindings", xOffset, yOffset)).addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				menus.push(new KeyBindingsMenu());
			}
		});
		if (CatacombSnatch.isPlayingGame()) {
			addButton(new Button("options.characterSelect", xOffset, yOffset += offset)).addListener(new ButtonAdapter() {
				@Override
				public void buttonPressed(ClickableComponent button) {
					menus.push(new CharacterSelectionMenu());
				}				
			});
		}
		addButton(new Button("options.sound_and_video", xOffset, yOffset += offset)).addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				menus.push(new AudioVideoMenu());
			}
		});
		addButton(new Button("options.locale_selection", xOffset, yOffset += offset)).addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				menus.push(new LocaleMenu());
			}
		});
		addButton(new Checkbox("options.creative", xOffset, yOffset += offset, Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE))).addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				creative = !creative;
				Options.set(Options.CREATIVE, creative);
			}
		});
		addButton(new Button("options.credits", xOffset, yOffset += offset)).addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				menus.push(new CreditsScreen());
			}
		});
		back = (Button)addButton(new Button("back", xOffset, (yOffset += offset) + 20));
		back.addListener(menus.BACK_BUTTON_LISTENER);
	}

	private void loadOptions() {
		creative = Options.getAsBoolean(Options.CREATIVE, Options.VALUE_FALSE);
	}

	@Override
	public void render(Screen screen) {
	    
		if (CatacombSnatch.isPlayingGame()) {
	    	screen.alphaFill(0, 0, screen.w, screen.h, 0xff000000, 0x30);
	    } else {
	    	screen.blit(Art.background, 0, 0);
	    }
		
		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("titlemenu.options"),
				screen.w / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons
				.get(selectedItem).getY() - 8);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			back.postClick();
		} else {
			super.keyPressed(e);
		}		
	}
}
