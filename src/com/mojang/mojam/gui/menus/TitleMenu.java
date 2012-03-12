package com.mojang.mojam.gui.menus;

import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class TitleMenu extends GuiMenu {
	public static final int BUTTON_SPACING = 2;

	public TitleMenu() {
		int y = 130;
		int x = (GameView.WIDTH - Button.WIDTH) / 2;
		try {
			addButton(new Button("titlemenu.start", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus.push(new LevelSelect(false));
				}
			});
			addButton(new Button("titlemenu.host", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus.push(new LevelSelect(true));
				}
			});
			addButton(new Button("titlemenu.join", x, y+=Button.HEIGHT+BUTTON_SPACING));
			addButton(new Button("titlemenu.help", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus.push(new HowToPlayMenu());
				}
			});
			addButton(new Button("titlemenu.options", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus.push(new OptionsMenu());
				}
			});
			addButton(new Button("titlemenu.levelEditor", x, y+=Button.HEIGHT+BUTTON_SPACING));
			addButton(new Button("titlemenu.exit", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus.exitGame();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0);
		// screen.blit(Art.titles[1], 0, 10);
		screen.blit(Art.titleScreen, 0, 0);

		super.render(screen);

		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(focusedItem).getX() - 40, 150 + focusedItem * (Button.HEIGHT + BUTTON_SPACING));

		// Display version number
		Font.FONT_GOLD_SMALL.draw(screen, Constants.GAME_VERSION, screen.w - 10, screen.h - 10, Font.Align.RIGHT);
	}
	
	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing
	}
}
