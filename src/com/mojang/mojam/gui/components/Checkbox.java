package com.mojang.mojam.gui.components;

import com.mojang.mojam.gui.Font;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Checkbox extends LabelledClickableComponent {
	public boolean checked = false;

	public static final int WIDTH = 140;
	public static final int HEIGHT = 19;

	public Checkbox(String label, int x, int y) {
		this(label, x, y, false);
	}

	public Checkbox(String staticTextsID, int x, int y, boolean checked) {
		super(x, y, 128, 24, staticTextsID);
		this.checked = checked;
	}

	protected void clicked() {
		checked = !checked;
	}

	public void render(Screen screen) {
		if (isPressed()) {
			if (checked)
				screen.blit(Art.checkbox[1][1], getX(), getY());
			else
				screen.blit(Art.checkbox[0][1], getX(), getY());
		} else {
			if (checked)
				screen.blit(Art.checkbox[1][0], getX(), getY());
			else
				screen.blit(Art.checkbox[0][0], getX(), getY());
		}

		Font.defaultFont().draw(screen, label, getX() + 24 + 4,
				getY() + getHeight() / 2 - 4);
		
		if (ClickableComponent.DEBUG_FOCUS && focused) {
			Font.defaultFont().draw(screen, "*", getX(), getY());
		}
	}
}
