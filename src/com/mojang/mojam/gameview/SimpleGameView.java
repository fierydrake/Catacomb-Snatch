package com.mojang.mojam.gameview;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.ScreenRenderer;
import com.mojang.mojam.SimpleGameElement;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gamelogic.GameMenus;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class SimpleGameView extends SimpleGameElement implements GameView {
	private GameMenus menus;

	private Screen screen = new Screen(GameView.WIDTH, GameView.HEIGHT);
	private ScreenRenderer renderer;
	
	private Player player;
	
	private long lastRenderTime;
	private int frames = 0;
	
	public SimpleGameView(GameMenus menus, ScreenRenderer renderer) {
		this.menus = menus;
		this.renderer = renderer;
	}
	
	public void renderView() {
		frames++;
		
		render();

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
	
	public void render() {
		player = logic().getLocalPlayer(); // FIXME
		if (CatacombSnatch.isPlayingGame()) {
			Level level = logic().getLevel();
			int xScroll = (int) (player.pos.x - screen.w / 2);
			int yScroll = (int) (player.pos.y - (screen.h - 24) / 2);
			
			level.render(screen, xScroll, yScroll);
			
			renderHealthBars();
			renderXpBar();
			renderScore();
				
//				if (gameLogic.isNetworkGame()) {
//					Font font = Font.defaultFont();
//					font.draw(screen, texts.latency(latencyCache.latencyCacheReady() ? "" + latencyCache.avgLatency() : "-"), 10, 20);
//					
//					chat.render(screen);
//				}
		}
		if (menus.isShowing()) {
			menus.getCurrent().render(screen);
		}
		// TODO
//		if (Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)) {
//			Font.defaultFont().draw(screen, texts.FPS(fps), 10, 10);
//		}

		// TODO
//		if(console.isOpen() && menuStack.isEmpty()) {
//			console.render(screen);
//		}

	
	}

	private void renderHealthBars() {
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
	
	private void renderXpBar() {
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
	
	private void renderScore() {
		screen.blit(Art.panel_coin, 314, screen.h - 55);
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().money(player.score), 335, screen.h - 52);
	}
}

