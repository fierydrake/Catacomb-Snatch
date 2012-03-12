package com.mojang.mojam.gui.components;

import java.awt.Point;
import java.awt.event.KeyEvent;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class Slider extends LabelledClickableComponent {
	public float value = 1.0f;
	private int pos = getX();

	public static final int WIDTH = 128;
	public static final int HEIGHT = 24;
	public static final int SLIDER_WIDTH = 16;

	public Slider(String label, int x, int y) {
		this(label, x, y, 1.0f);
	}

	public Slider(String staticTextsID, int x, int y, float value) {
		super(x, y, 128, 24, staticTextsID);
		this.value = value;
		this.pos = (int) ((float) (getX() + getWidth() - SLIDER_WIDTH - getX()) * value) + getX();
	}
	
	@Override
	public void tick(LocalGameInput input) {
		super.tick(input);

		Point mousePos = input.getCurrentPhysicalState().getMousePosition();
		if (isPressed() && contains(mousePos)) {
			pos = Mth.clamp((int) mousePos.x - (SLIDER_WIDTH / 2), getX(), getX() + getWidth()
					- SLIDER_WIDTH);
			float newValue = 1.0f / (float) (getX() + getWidth() - SLIDER_WIDTH - getX())
					* (float) (pos - getX());

			if (newValue != value) {
				value = newValue;
				postClick();
			}
		}
		if (focused) {
			if (input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
				setValue(Mth.clamp(value - 0.1f, 0.0f, 1.0f));
				postClick();
			}
			if (input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
				setValue(Mth.clamp(value + 0.1f, 0.0f, 1.0f));
				postClick();
			}
			if (input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_ENTER)) {
				setValue(value == 1.0f ? 0.0f : 1.0f);
				postClick();
			}
		}
	}

	@Override
	public void render(Screen screen) {
		screen.alphaFill(getX() + SLIDER_WIDTH, getY(), getWidth() - SLIDER_WIDTH * 2, getHeight(), 0xff000000, 0x80);
	    screen.alphaBlit(Art.slider[1][0], getX(), getY(), 0x80);
		screen.alphaBlit(Art.slider[1][1], getX() + getWidth() - SLIDER_WIDTH, getY(), 0x80);

		if (isPressed())
			screen.blit(Art.slider[0][1], pos, getY());
		else
			screen.blit(Art.slider[0][0], pos, getY());

		String view = "";

		if (value == 0.0f)
			view = Texts.current().getStatic("options.mute");
		else
			view = (Math.round(value * 100.0f)) + "%";

		Font.defaultFont().draw(screen, label + ": " + view, getX() + getWidth() / 2, getY()
				+ getHeight() / 2, Font.Align.CENTERED);
		
		if (ClickableComponent.DEBUG_FOCUS && focused) {
			Font.defaultFont().draw(screen, "*", getX(), getY());
		}
	}

	public void setValue(float value) {
		this.value = value;
		pos = (int) ((float) (getX() + getWidth() - SLIDER_WIDTH - getX()) * value) + getX();
	}

	@Override
	protected void clicked() {}
}
