package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class PauseMenu extends GuiMenu {
	private Button resumeButton;

	public PauseMenu() {
		super();

		resumeButton = (Button) addButton(new BackButton("pausemenu.resume", (GameView.WIDTH - 128) / 2, 140));
		addButton(new Button("pausemenu.help", (GameView.WIDTH - 128) / 2, 170) {
			@Override
			public void clicked() {
				menus.push(new HowToPlayMenu());
			}
		});
		addButton(new Button("titlemenu.options", (GameView.WIDTH - 128) / 2, 200) {
			@Override
			public void clicked() {
				menus.push(new OptionsMenu());
			}
		});
		addButton(new Button("pausemenu.backtomain", (GameView.WIDTH - 128) / 2, 230) {
			@Override
			public void clicked() {
				menus.stopPlaying();
			}
		}); // TODO: TitleMenu.RETURN_TO_TITLESCREEN
		addButton(new Button("pausemenu.exit", (GameView.WIDTH - 128) / 2, 260) {
			@Override
			public void clicked() {
				menus.exitGame();
			}
		});

	}

	public void render(Screen screen) {
		super.render(screen);
		screen.blit(Art.pauseScreen, 0, 0);
		screen.blit(Art.getLocalPlayerArt()[0][6], (screen.w - 128) / 2 - 40, 130 + focusedItem * 30);
	}
	
	
	@Override
	public void tick(LocalGameInput input) {
		if (input.getCurrentPhysicalState().wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
			resumeButton.postClick();
		}		
		super.tick(input);
	}
	
	@Override
	public void buttonPressed(ClickableComponent button) {}
}
