package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;

import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class PauseMenu extends GuiMenu {
	private Button resumeButton;

	public PauseMenu() {
		super();

		resumeButton = (Button) addButton(new Button("pausemenu.resume", (GameView.WIDTH - 128) / 2, 140)); // TODO: TitleMenu.RETURN_ID
		addButton(new Button("pausemenu.help", (GameView.WIDTH - 128) / 2, 170)); // TODO: TitleMenu.HOW_TO_PLAY
		addButton(new Button("titlemenu.options", (GameView.WIDTH - 128) / 2, 200)); // TODO: TitleMenu.OPTIONS_ID
		addButton(new Button("pausemenu.backtomain", (GameView.WIDTH - 128) / 2, 230)); // TODO: TitleMenu.RETURN_TO_TITLESCREEN
		addButton(new Button("pausemenu.exit", (GameView.WIDTH - 128) / 2, 260)); // TODO: TitleMenu.EXIT_GAME_ID

	}

	public void render(Screen screen) {

		//screen.clear(0);
		//screen.blit(Art.emptyBackground, 0, 0);
	    screen.alphaFill(0, 0, screen.w, screen.h, 0xff000000, 0x30);
		screen.blit(Art.pauseScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.getLocalPlayerArt()[0][6], (screen.w - 128) / 2 - 40,
				130 + selectedItem * 30);
	}
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			resumeButton.postClick();
		} else {
			super.keyPressed(e);
		}		
	}
	
	@Override
	public void buttonPressed(ClickableComponent button) {
		// TODO Auto-generated method stub

	}
}
