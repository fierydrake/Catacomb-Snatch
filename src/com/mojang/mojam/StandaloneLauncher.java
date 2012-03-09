package com.mojang.mojam;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mojang.mojam.resources.Constants;
import com.mojang.mojam.screen.Art;

/**
 * This class launches Catacomb Snatch as an application 
 * @author fierydrake
 */
public class StandaloneLauncher {
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
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GameWindow(CatacombSnatch.canvas, Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE));
				CatacombSnatch.start();
			}
		});
	}
}
