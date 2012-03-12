package com.mojang.mojam.gui;

import com.mojang.mojam.gui.components.ClickableComponent;

public interface ButtonListener {

	public void buttonPressed(ClickableComponent button);

	public void buttonHovered(ClickableComponent clickableComponent);
}
