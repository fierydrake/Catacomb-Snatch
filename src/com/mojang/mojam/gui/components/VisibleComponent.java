package com.mojang.mojam.gui.components;

import java.awt.Point;

import com.mojang.mojam.gui.GuiElement;
import com.mojang.mojam.screen.Screen;

public class VisibleComponent extends GuiElement {

	private int x;
	private int y;
	private int w;
	private int h;

	@Override
	public void render(Screen screen) {
		super.render(screen);
	}

	public VisibleComponent(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}
	
	public void setX(int x) { 
		this.x=x; 
	}
	
	public void setY(int y) { 
		this.y=y; 
	}
	
	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}
	
	public boolean contains(int px, int py) {
		return (px >= x && px <= x+w && py >= y && py <= y+h);
	}
}
