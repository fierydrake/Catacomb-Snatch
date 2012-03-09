package com.mojang.mojam.gameview;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.ScreenRenderer;
import com.mojang.mojam.SimpleGameElement;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gamelogic.GameMenus;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.screen.Screen;

public class SimpleGameView extends SimpleGameElement implements GameView {
	private GameMenus menus;

	private Screen screen = new Screen(GameView.WIDTH, GameView.HEIGHT);
	private ScreenRenderer renderer;
	
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
		if (CatacombSnatch.isPlayingGame()) {
			Level level = logic().getLevel();
			Player player = logic().getLocalPlayer();
			int xScroll = (int) (player.pos.x - screen.w / 2);
			int yScroll = (int) (player.pos.y - (screen.h - 24) / 2);
			
			level.render(screen, xScroll, yScroll);
			
//			if (!gameLogic.getMenus().isShowing()) {
				// TODO
//				renderHealthBars(screen);
//				renderXpBar(screen);
//				renderScore(screen);
//				
//				if (gameLogic.isNetworkGame()) {
//					Font font = Font.defaultFont();
//					font.draw(screen, texts.latency(latencyCache.latencyCacheReady() ? "" + latencyCache.avgLatency() : "-"), 10, 20);
//					
//					chat.render(screen);
//				}
//			}
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
	
}
