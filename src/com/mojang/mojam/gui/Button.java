package com.mojang.mojam.gui;

import com.mojang.mojam.MouseButtons;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class Button extends LabelledClickableComponent {

    public static final int WIDTH = 128;
    public static final int HEIGHT = 24;
    
    private Bitmap mainBitmap = null;
    private Bitmap rightBorderBitmap = null;
    private Bitmap middleBitmap = null;

    public Button(String staticTextsID, int x, int y) {
    	this(staticTextsID, x, y, WIDTH, HEIGHT, true);
    }
    
	public Button(String staticTextsID, int x, int y, boolean isLocaleAware) {
		this(staticTextsID, x, y, WIDTH, HEIGHT, isLocaleAware);
	}

    public Button(String staticTextsID, int x, int y, int w, int h) {
    	this(staticTextsID, x, y, w, h, false);
    }
    
    public Button(String staticTextsID, int x, int y, int w, int h, boolean isLocaleAware) {
        super(x, y, w, h, staticTextsID, isLocaleAware);
    }
    
	@Override
	protected void clicked(MouseButtons mouseButtons) {
		// do nothing, handled by button listeners
	}

	@Override
	public void render(Screen screen) {

		if(enabled){
			if (isPressed()) {
			    blitBackground(screen, 1);
			} else {
			    blitBackground(screen, 0);
			}
		} else {
			blitBackground(screen, 2);
		}
		
		if (Font.defaultFont().calculateStringWidth(label) > getWidth()) {
			String truncatedLabel = label;
			while (Font.defaultFont().calculateStringWidth(truncatedLabel + "...") > getWidth()) {
				truncatedLabel = truncatedLabel.substring(0, truncatedLabel.length() - 2);
			}
			Font.defaultFont().draw(screen, truncatedLabel + "...", getX() + getWidth() / 2, getY() + getHeight() / 2, Font.Align.CENTERED);
		} else {
			Font.defaultFont().draw(screen, label, getX() + getWidth() / 2, getY() + getHeight() / 2, Font.Align.CENTERED);
		}
	}
	
	protected void blitBackground(Screen screen, int bitmapId) {
	    
	    // Default width button
	    if (getWidth() == WIDTH) {
            screen.blit(Art.button[0][bitmapId], getX(), getY());
	    }
	    
	    // Custom width buttons
	    else {
    	    // Cut button textures
    	    if (mainBitmap != Art.button[0][bitmapId]) {
    	        mainBitmap = Art.button[0][bitmapId];
    	        rightBorderBitmap = new Bitmap(10, HEIGHT);
    	        rightBorderBitmap.blit(mainBitmap, - WIDTH + 10, 0);
                middleBitmap = new Bitmap(1, HEIGHT);
                middleBitmap.blit(mainBitmap, -10, 0);
    	    }
    	    
    	    // Draw button
            screen.blit(mainBitmap, getX(), getY(), 10, getHeight());
            for (int x = getX() + 10; x < getX() + getWidth() - 10; x++) {
                screen.blit(middleBitmap, x, getY());
            }
            screen.blit(rightBorderBitmap, getX() + getWidth() - 10, getY());
	    }
	}
}
