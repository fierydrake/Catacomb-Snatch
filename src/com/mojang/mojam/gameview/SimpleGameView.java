package com.mojang.mojam.gameview;

import static com.mojang.mojam.CatacombSnatch.game;
import static com.mojang.mojam.CatacombSnatch.logic;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameInformation.Type;
import com.mojang.mojam.LatencyCache;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gamelogic.SyncServerGameLogic;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class SimpleGameView implements GameView {
	
	private Player player;
	
	/* Extent of the viewport in level coordinates */
	private int viewportBoundsX, viewportBoundsY;
	private int viewportBoundsMX, viewportBoundsMY;
	private int viewportWidth, viewportHeight;
	
	private long lastFPSupdate = 0;
	private int frames = 0;
	private int fps = 0;
	
	public SimpleGameView() {
		viewportWidth = GameView.WIDTH; /* TODO? Make configurable by passing these in */
		viewportHeight = GameView.HEIGHT; /* TODO? Make configurable by passing these in */
	}

	@Override public void setPlayer(Player player) { this.player = player; }
	@Override public Player getPlayer() { return player; }
	
	@Override public int getViewportBoundsX() { return viewportBoundsX; }
	@Override public int getViewportBoundsY() { return viewportBoundsY; }
	@Override public int getViewportBoundsMX() { return viewportBoundsMX; }
	@Override public int getViewportBoundsMY() { return viewportBoundsMY; }
	@Override public int getViewportWidth() { return viewportWidth; }
	@Override public int getViewportHeight() { return viewportHeight; }

	public void renderView(Screen screen) {
		frames++;
		if (System.currentTimeMillis() - lastFPSupdate >= 1000) {
			lastFPSupdate = System.currentTimeMillis();
			fps = frames;
			frames = 0;
		}
		
		render(screen);
	}
	
	private void render(Screen screen) {
		if (!CatacombSnatch.isPlayingGame() || player == null) return;
		
		/* Update viewport bounds */
		viewportBoundsX = (int) (player.pos.x - screen.w / 2);;
		viewportBoundsY = (int) (player.pos.y - (screen.h - 24) / 2);
		viewportBoundsMX = viewportBoundsX + viewportWidth;
		viewportBoundsMY = viewportBoundsY + viewportHeight;
		
		logic().getLevel().render(screen, this);
			
		// TODO? Most of the rest of the HUD is rendered in Level.render()
		//       maybe these should too (or visa versa)
		renderHealthBars(screen);
		renderXpBar(screen);
		renderScore(screen);
		
//				if (gameLogic.isNetworkGame()) {
//					
//					chat.render(screen);
//				}
		
		// Render FPS
		if (Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)) {
			Font.defaultFont().draw(screen, Texts.current().FPS(fps), 10, 10);
		}
		
		if (game().type == Type.SYNCHED_NETWORK) {
			LatencyCache latencyCache = ((SyncServerGameLogic)logic()).latencyCache;
			Font font = Font.defaultFont();
			font.draw(screen, Texts.current().latency(latencyCache.latencyCacheReady() ? "" + latencyCache.avgLatency() : "-"), 10, 20);
		}
		
		// DEBUG: Render movement key
//		GameInput input = player.getInput();
//		String debug = (input.getCurrentState().up.isDown ? "UP " : "up ") + (input.getCurrentState().down.isDown ? "DOWN " : "down ") +
//				(input.getCurrentState().left.isDown ? "LEFT " : "left ") + (input.getCurrentState().right.isDown ? "RIGHT" : "right");
//		Font.defaultFont().draw(screen, debug, 10, 20);

		// TODO
//		if(console.isOpen() && menuStack.isEmpty()) {
//			console.render(screen);
//		}

	
	}

	private void renderHealthBars(Screen screen) {
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
	
	private void renderXpBar(Screen screen) {
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
	
	private void renderScore(Screen screen) {
		screen.blit(Art.panel_coin, 314, screen.h - 55);
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().money(player.score), 335, screen.h - 52);
	}
}

