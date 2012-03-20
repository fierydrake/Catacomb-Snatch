package com.mojang.mojam.gui.menus;

import static com.mojang.mojam.CatacombSnatch.game;
import static com.mojang.mojam.CatacombSnatch.menus;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.Options;
import com.mojang.mojam.PlayerInformation;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gameview.SimpleGameView;
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
					game().type = GameInformation.Type.SINGLE_PLAYER;
					game().players.clear();
					game().players.add(new PlayerInformation(Team.Team1, Options.getCharacter(), (GameInput)CatacombSnatch.getLocalInput(), new SimpleGameView()));
					menus().push(new LevelSelect(false));
				}
			});
			addButton(new Button("titlemenu.host", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					game().type = GameInformation.Type.SYNCHED_NETWORK;
					game().players.clear();
					game().players.add(new PlayerInformation(Team.Team1, Options.getCharacter(), (GameInput)CatacombSnatch.getLocalInput(), new SimpleGameView()));
					menus().push(new LevelSelect(true));
				}
			});
			addButton(new Button("titlemenu.join", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					game().type = GameInformation.Type.SYNCHED_NETWORK;
					game().players.clear();
					game().players.add(new PlayerInformation(Team.Team2, Options.getCharacter(), (GameInput)CatacombSnatch.getLocalInput(), new SimpleGameView()));
					menus().push(new JoinGameMenu());
				}
			});
			addButton(new Button("titlemenu.help", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus().push(new HowToPlayMenu());
				}
			});
			addButton(new Button("titlemenu.options", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus().push(new OptionsMenu());
				}
			});
			addButton(new Button("titlemenu.levelEditor", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					menus().push(new LevelEditorMenu());
				}
			});
			addButton(new Button("titlemenu.exit", x, y+=Button.HEIGHT+BUTTON_SPACING) {
				@Override
				public void clicked() {
					CatacombSnatch.exit();
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
