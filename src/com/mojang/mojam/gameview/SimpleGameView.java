package com.mojang.mojam.gameview;

import java.awt.Point;

import com.mojang.mojam.GameMenus;
import com.mojang.mojam.Options;
import com.mojang.mojam.ScreenRenderer;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gamelogic.GameLogic;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class SimpleGameView implements GameView {
	private Screen screen = new Screen(GameView.WIDTH, GameView.HEIGHT);
	private ScreenRenderer renderer;
	
	private long lastFPSupdate = 0;
	private int frames = 0;
	private int fps = 0;
	
	public SimpleGameView(ScreenRenderer renderer) {
		this.renderer = renderer;
	}
	
	public void renderView(LocalGameInput input, GameMenus menus, GameLogic logic) {
		frames++;
		if (System.currentTimeMillis() - lastFPSupdate >= 1000) {
			lastFPSupdate = System.currentTimeMillis();
			fps = frames;
			frames = 0;
		}
		
		render(input, menus, logic);

		// Render mouse
		// TODO
//		renderMouse(screen, mouseButtons);

//		long renderTime = System.nanoTime();
//		int timePassed = (int) (renderTime - lastRenderTime);
//		if (timePassed < min) {
//			min = timePassed;
//		}
//		if (timePassed > max) {
//			max = timePassed;
//		}
//		lastRenderTime = renderTime;
		
		renderer.render(this, screen, GameView.WIDTH * GameView.SCALE, GameView.HEIGHT * GameView.SCALE);
	}
	
	public void render(LocalGameInput input, GameMenus menus, GameLogic logic) {
		if (menus.isPlayingGame()) {
			Player player = logic.getLocalPlayer();
			int xScroll = (int) (player.pos.x - screen.w / 2);
			int yScroll = (int) (player.pos.y - (screen.h - 24) / 2);
			
			logic.getLevel().render(screen, xScroll, yScroll);
			
			renderHealthBars(logic);
			renderXpBar(logic);
			renderScore(logic);
			
				
//				if (gameLogic.isNetworkGame()) {
//					Font font = Font.defaultFont();
//					font.draw(screen, texts.latency(latencyCache.latencyCacheReady() ? "" + latencyCache.avgLatency() : "-"), 10, 20);
//					
//					chat.render(screen);
//				}
		}
		if (menus.isShowing()) {
			if (menus.isPlayingGame()) {
				screen.alphaFill(0, 0, screen.w, screen.h, 0xff000000, 0xC0);
			} else {
				screen.blit(Art.background, 0, 0);
			}
			menus.getCurrent().render(screen);
		}
		
		// Render mouse pointer
		if (menus.isMouseActive()) {
			renderMouse(input.getCurrentPhysicalState().getMousePosition());
		}
		
		// Render FPS
		if (Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)) {
			Font.defaultFont().draw(screen, Texts.current().FPS(fps), 10, 10);
		}
		
		// Render movement key debug
		String debug = (input.getCurrentState().up.isDown ? "UP " : "up ") + (input.getCurrentState().down.isDown ? "DOWN " : "down ") +
				(input.getCurrentState().left.isDown ? "LEFT " : "left ") + (input.getCurrentState().right.isDown ? "RIGHT" : "right");
		Font.defaultFont().draw(screen, debug, 10, 20);

		// TODO
//		if(console.isOpen() && menuStack.isEmpty()) {
//			console.render(screen);
//		}

	
	}

	private void renderHealthBars(GameLogic logic) {
		Player player = logic.getLocalPlayer();
		int maxIndex = Art.panel_healthBar[0].length - 1;
		int index = maxIndex - Math.round(player.health * maxIndex / player.maxHealth);
		if (index < 0)
			index = 0;
		else if (index > maxIndex)
			index = maxIndex;

		screen.blit(Art.panel_healthBar[0][index], 311, screen.h - 17);
		screen.blit(Art.panel_heart, 314, screen.h - 24);
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().health(player.health, player.maxHealth), 335, screen.h - 21);	
	}
	
	private void renderXpBar(GameLogic logic) {
		Player player = logic.getLocalPlayer();
		int xpSinceLastLevelUp = (int) (player.xpSinceLastLevelUp());
		int xpNeededForNextLevel = (int) (player.nettoXpNeededForLevel(player.plevel + 1));
		
		int maxIndex = Art.panel_xpBar[0].length - 1;
		int index = maxIndex - Math.round(xpSinceLastLevelUp * maxIndex / xpNeededForNextLevel);
		if (index < 0)
			index = 0;
		else if (index > maxIndex)
			index = maxIndex;
		
		screen.blit(Art.panel_xpBar[0][index], 311, screen.h - 32);
		screen.blit(Art.panel_star, 314, screen.h - 40);
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().playerLevel(player.plevel + 1), 335, screen.h - 36);
	}
	
	private void renderScore(GameLogic logic) {
		Player player = logic.getLocalPlayer();
		screen.blit(Art.panel_coin, 314, screen.h - 55);
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().money(player.score), 335, screen.h - 52);
	}
	
	private void renderMouse(Point pos) {
		int crosshairSize = 15;
		int crosshairSizeHalf = crosshairSize / 2;

		Bitmap marker = new Bitmap(crosshairSize, crosshairSize);

		// horizontal line
		for (int i = 0; i < crosshairSize; i++) {
			if (i >= crosshairSizeHalf - 1 && i <= crosshairSizeHalf + 1)
				continue;

			marker.pixels[crosshairSizeHalf + i * crosshairSize] = 0xffffffff;
			marker.pixels[i + crosshairSizeHalf * crosshairSize] = 0xffffffff;
		}

		screen.blit(marker, pos.x - crosshairSizeHalf - 2, pos.y - crosshairSizeHalf - 2);
	}
}

