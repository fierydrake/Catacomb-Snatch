package com.mojang.mojam.gameinput;

import java.awt.Point;

import com.mojang.mojam.network.packet.ChangeLogicalInputCommand;
import com.mojang.mojam.network.packet.ChangeMouseCoordinateCommand;

public class NetworkGameInput extends BaseGameInput {

	private boolean mouseActive = false;
	private Point mousePosition = new Point(0, 0);
	
	private boolean nextMouseActive = false;
	private Point nextMousePosition = new Point(0, 0);

	@Override
	public boolean isMouseActive() { return mouseActive; }

	@Override
	public Point getMousePosition() { return mousePosition; }

	@Override
	public void tick() {
		super.tick();
		mouseActive = nextMouseActive;
		mousePosition = nextMousePosition;
	}
	
	public void updateLogicalInput(ChangeLogicalInputCommand clic) {
		if (next.all.containsKey(clic.getLogicalInputName())) {
			clic.copyInto(next.all.get(clic.getLogicalInputName()));
		}
	}

	public void updateMouse(ChangeMouseCoordinateCommand ccc) {
		nextMousePosition = new Point(ccc.getX(), ccc.getY());
		nextMouseActive = !ccc.isMouseHidden();
	}
}
