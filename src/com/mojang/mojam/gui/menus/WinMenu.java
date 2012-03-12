package com.mojang.mojam.gui.menus;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class WinMenu extends GuiMenu {
	private Team winningPlayer;
	private GameCharacter character;

	public WinMenu(Team winningPlayer, GameCharacter character) {
		super();
		this.winningPlayer = winningPlayer;
		this.character = character;

		addButton(new Button("Ok", (GameView.WIDTH - 128) / 2, 200)); // TODO: TitleMenu.RETURN_TO_TITLESCREEN
	}

	@Override
	public void render(Screen screen) {
		screen.clear(0);
		screen.blit(Art.gameOverScreen, 0, 0);

		Font.defaultFont().draw(screen, Texts.current().winCharacter(winningPlayer, character), 180, 160);

		super.render(screen);

		screen.blit(Art.getPlayer(character)[0][6], (screen.w - 128) / 2 - 40, 190 + focusedItem * 40);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
