package com.mojang.mojam.level.tile;

import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;

public class UnbreakableRailTile extends RailTile {
	public static final int COLOR = 0xff969696;

	public UnbreakableRailTile(Tile parent) {
		super(parent);
	}

    @Override
	public boolean remove() {
		return false;
	}
	
    @Override
	public int getColor() {
		return UnbreakableRailTile.COLOR;
	}

    @Override
	public String getName() {
		return RailTile.NAME;
	}

	@Override
	public Bitmap getBitMapForEditor() {
		return Art.rails[1][0];
	}
	
	@Override
	public int getMiniMapColor() {
		return minimapColor;
	}
}
