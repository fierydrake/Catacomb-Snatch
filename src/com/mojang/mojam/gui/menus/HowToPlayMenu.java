package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.Panel;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HowToPlayMenu extends GuiMenu {

	private int goalX = 350;
	private int imgTab = 70;
	private int tab1 = 100;
	private int tab2 = 210;
	private int goalTopMargin = 20;
	private int vspace = 55;
	
	public HowToPlayMenu() {
		super();
		
	    addButton(new BackButton(GameView.WIDTH - 128 - 20, GameView.HEIGHT - 24 - 25));
		
		// Background panels
		addButton(new Panel(goalX - 155, goalTopMargin - 15, 314, 78));
		addButton(new Panel(goalX - 155, goalTopMargin + 55 + 10, 314, 235));
		addButton(new Panel(imgTab - 67, goalTopMargin + 55 + 10, 189, 235));
	}

	@Override
	public void tick(LocalGameInput input) {
		if (input.getCurrentPhysicalState().wasKeyPressedConsume(KeyEvent.VK_ESCAPE, KeyEvent.VK_ENTER)) {
			buttons.get(0).postClick();
		}
		super.tick(input);
	}
	
	@Override
	public void render(Screen screen) {
		super.render(screen);
		printHelpText(screen);
		
		// Back button character
		screen.blit(Art.getLocalPlayerArt()[0][6], screen.w - 128 - 20 - 40, screen.h - 24 - 25 - 8);
	}

	private void printHelpText(Screen screen) {

		// Game goal
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().getStatic("help.goal") + ":", goalX, goalTopMargin, Font.Align.CENTERED);
		font.draw(screen, Texts.current().getStatic("help.help1"), goalX, goalTopMargin+10, Font.Align.CENTERED);
		font.draw(screen, Texts.current().getStatic("help.help2"), goalX, goalTopMargin+20, Font.Align.CENTERED);
		font.draw(screen, Texts.current().getStatic("help.help3"), goalX, goalTopMargin+30, Font.Align.CENTERED);
		font.draw(screen, Texts.current().getStatic("help.help4"), goalX, goalTopMargin+40, Font.Align.CENTERED);
		
		// Turret
		int line = 102;
		screen.blit(Art.turret[7][0], imgTab-60, line);
		screen.blit(Art.turret2[7][0], imgTab-30, line);
		screen.blit(Art.turret3[7][0], imgTab, line);
		font.draw(screen, Texts.current().getStatic("help.turret"), tab1, line);
		screen.blit(Art.pickupGemEmerald[0][0], tab1, line+10);
		screen.blit(Art.pickupCoinGold[0][0], tab1+15, line+10);
		font.draw(screen, "150", tab1+35, line+15);
		font.draw(screen, Texts.current().getStatic("help.turret1"), tab2, line);
		font.draw(screen, Texts.current().getStatic("help.turret2"), tab2, line+10);
		font.draw(screen, Texts.current().getStatic("help.turret3"), tab2, line+20);

		// Harvester
		line += vspace;
		screen.blit(Art.harvester[7][0], imgTab-60, line-10);
		screen.blit(Art.harvester2[7][0], imgTab-30, line-10);
		screen.blit(Art.harvester3[7][0], imgTab, line-10);
		font.draw(screen, Texts.current().getStatic("help.collector"), tab1, line);
		screen.blit(Art.pickupGemEmerald[0][0], tab1, line+10);
		screen.blit(Art.pickupGemRuby[0][0], tab1+15, line+10);
		font.draw(screen, "300", tab1+35, line+15);
		font.draw(screen, Texts.current().getStatic("help.collector1"), tab2, line);
		font.draw(screen, Texts.current().getStatic("help.collector2"), tab2, line+10);
		font.draw(screen, Texts.current().getStatic("help.collector3"), tab2, line+20);

		// Bomb
		line += vspace;
		screen.blit(Art.bomb, imgTab, line);
		font.draw(screen, Texts.current().getStatic("help.bomb"), tab1, line);
		screen.blit(Art.pickupGemDiamond[3][0], tab1, line+10);
		font.draw(screen, "500", tab1+30, line+15);
		font.draw(screen, Texts.current().getStatic("help.bomb1"), tab2, line);
		font.draw(screen, Texts.current().getStatic("help.bomb2"), tab2, line+10);
		font.draw(screen, Texts.current().getStatic("help.bomb3"), tab2, line+20);
		
		// Rail
		line += vspace;
		screen.blit(Art.rails[1][0], imgTab, line);
		font.draw(screen, Texts.current().getStatic("help.rails"), tab1, line);
		screen.blit(Art.pickupCoinBronze[0][0], tab1+10, line+15);
		font.draw(screen, "10", tab1+30, line+20);
		font.draw(screen, Texts.current().getStatic("help.rails1"), tab2, line);
		font.draw(screen, Texts.current().getStatic("help.rails2"), tab2, line+10);
		font.draw(screen, Texts.current().getStatic("help.rails3"), tab2, line+20);
        
        // Panel separation lines
        for (int i = 0; i < 3; i++) {
        	screen.fill(8, 150 + 55 * i, 180, 1, 0xFF442200);
        	screen.fill(200, 150 + 55 * i, 305, 1, 0xFF442200);
        }
	}

	@Override
	public void buttonPressed(ClickableComponent button) {	
	}

}
