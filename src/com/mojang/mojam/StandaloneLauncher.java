package com.mojang.mojam;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.screen.Art;

/**
 * This class launches Catacomb Snatch as an application 
 * @author fierydrake
 */
public class StandaloneLauncher implements Launcher {
	public static class GameWindow extends JFrame {
		private static final long serialVersionUID = 1L;
		public GameWindow(Canvas canvas, boolean fullscreen) {
			super(Constants.GAME_TITLE);
			
			JPanel panel = new JPanel();
			setContentPane(panel);
			panel.setLayout(new BorderLayout());
			panel.add(canvas);
			pack();
			canvas.requestFocusInWindow();

			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			ArrayList<BufferedImage> icoList = new ArrayList<BufferedImage>();
			icoList.add(Art.icon32);
			icoList.add(Art.icon64);
			setIconImages(icoList);
			
			setVisible(true);
			setFullscreen(fullscreen);
		}
		
		public void toggleFullscreen() {
			setFullscreenInternal(!isFullscreenInternal());
		}
		
		public void setFullscreen(boolean fullscreen) {
			if (fullscreen != isFullscreenInternal()) {
				setFullscreenInternal(fullscreen);
			}
		}
		
		private synchronized void setFullscreenInternal(boolean fullscreen) {
			// TODO
		}
		
		private synchronized boolean isFullscreenInternal() {
			return false; // TODO
		}
	}
	
	private GameWindow window;
	private CatacombSnatchCanvas canvas;
	
	@Override
	public void toggleFullscreen() {
		runOnSwingDispatcherThread(new Runnable() {
			@Override
			public void run() {
				window.toggleFullscreen();
			}
		});
	}
	
	@Override
	public void setFullscreen(final boolean fullscreen) {
		runOnSwingDispatcherThread(new Runnable() {
			@Override
			public void run() {
				window.setFullscreen(fullscreen);
			}
		});
	}
	public void setRendererSize(Dimension size) {}
	public void setRendererScale(int scale) {}

	public void preferredSizeChanged(final Dimension preferredSize) {
		runOnSwingDispatcherThread(new Runnable() {
			@Override
			public void run() {
				// TODO Check this actually works
				canvas.setPreferredSize(preferredSize);
				canvas.invalidate();
				window.validate();
			}
		});
	}
	
	public void preferredFullscreenChanged(final boolean fullscreen) {
	}
	
	public void launch() {
		Dimension preferredSize = CatacombSnatch.getRendererSize();
		boolean fullscreen = CatacombSnatch.getFullscreen();
		int scale = CatacombSnatch.getRendererScale();
		
		canvas = new CatacombSnatchCanvas(preferredSize, scale);
		window = new GameWindow(canvas, fullscreen);
		
		CatacombSnatch.start(this, new LocalGameInput(canvas, scale), (ScreenRenderer)canvas);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new StandaloneLauncher().launch();
			}
		});
	}
	
	/* Utility methods */
	
	public static void runOnSwingDispatcherThread(Runnable runnable) {
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
