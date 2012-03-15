package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class CreditsScreen extends GuiMenu {
	private Button back;
	
	public String officialGame   = "Mojang AB";
	public String[] leadDev      = {"Maescool"};
	public String[] officialDev  = {"Borsty", "danielduner", "flet", "judgedead53", "Maescool",
			"master-lincoln", "mkalam-alami", "Scorpion1122"};
	public String[] communityMan = {"Austin01", "zorro300"};
	public String[] others = {"xPaw", "BubblegumBalloon", "Elosanda", "GreenLightning", "Mebibyte", "Hammy55"};

	public CreditsScreen() {
		super();
		
		back = (Button)addButton(new BackButton((GameView.WIDTH - 128) / 2, GameView.HEIGHT - 50 - 10));
	}

    @Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);

		// Mojang logo
		screen.blit(Art.mojangLogo, (screen.w - Art.mojangLogo.w) / 2, 30);
		
		Font font = Font.defaultFont();
		Texts txts = Texts.current();
		int x = (screen.w - 512) / 2 + 30;
		int y = 80;
		font.draw(screen, "* " + txts.getStatic("credits.note"), x, y);
		font.draw(screen, txts.getStatic("credits.leadDev"), x, y += 30);
		font.draw(screen, txts.getStatic("credits.maintainers"), x, y += 30);
		font.draw(screen, txts.getStatic("credits.communityMan"), x, y += 60);
		font.draw(screen, txts.getStatic("credits.others"), x, y += 30);

		font = Font.FONT_GRAY;
		drawNames(leadDev, screen, 120);
		drawNames(officialDev, screen, 150);
		drawNames(communityMan, screen, 210);
		drawNames(others, screen, 240);

		// Back button character
		screen.blit(Art.getLocalPlayerArt()[0][6], (screen.w - 128) / 2 - 40, screen.h - 50 - 20);
	}
	
	public int drawNames(String[] names, Screen screen, Integer y) {
		List<Vector<String>> data = new Vector<Vector<String>>();
		data.add(new Vector<String>());
		data.add(new Vector<String>());
		for (int i = 0; i < names.length; i++) {
			String string = names[i];
			if (i % 2 == 0) {
				Vector<String> tmp = data.get(0);
				tmp.add(string);
				data.set(0, tmp);
			} else {
				Vector<String> tmp = data.get(1);
				tmp.add(string);
				data.set(1, tmp);
			}
		}
		int groupId  = 0;
		int drawY    = y;
		int drawX    = 50;
		int xOffset  = 200;
		int yOffset  = 10;
		int highestY = drawY;
		for (Vector<String> group: data) {
			drawX += xOffset * groupId;
			for (String name : group) {
				Font.defaultFont().draw(screen, name, drawX, drawY);
				if (drawY > highestY) {
					highestY = drawY;
				}
				drawY += yOffset;
			}
			drawY = y;
			++groupId;
		}
		return highestY;
	}

	@Override
	public void tick(LocalGameInput input) {
		if (input.getCurrentPhysicalState().wasKeyPressedConsume(KeyEvent.VK_ESCAPE, KeyEvent.VK_ENTER)) {
			back.postClick();
		}
		super.tick(input);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}
}
