package com.mojang.mojam.gui.components;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class CharacterButton extends Button {

	public static final int WIDTH = 160;
	public static final int HEIGHT = 64;

	private static Bitmap backgrounds[] = new Bitmap[3];
	static {
		backgrounds[0] = new Bitmap(WIDTH, HEIGHT);
		backgrounds[0].fill(0, 0, WIDTH, HEIGHT, 0xff522d16);
		backgrounds[0].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
		backgrounds[1] = new Bitmap(WIDTH, HEIGHT);
		backgrounds[1].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		backgrounds[1].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0);
		backgrounds[2] = new Bitmap(WIDTH, HEIGHT);
		backgrounds[2].fill(0, 0, WIDTH, HEIGHT, 0xff26150a);
		backgrounds[2].fill(1, 1, WIDTH - 2, HEIGHT - 2, 0xff3a210f);
	}

	private GameCharacter character;
	private Bitmap characterArt;
	private boolean selected;
	
	public CharacterButton(GameCharacter character, Bitmap characterArt, int x, int y) {
		super(Texts.current().playerNameCharacter(character), x, y, WIDTH, HEIGHT);
		this.character = character;
		this.characterArt = characterArt;
	}
	
	@Override
	public String labelText() { 
		return Texts.current().playerNameCharacter(character);
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public GameCharacter getCharacter() {
		return character;
	}

	@Override
	public void render(Screen screen) {
		screen.blit(backgrounds[isSelected() ? 2 : (isPressed() || focused ? 1 : 0)], getX(), getY());
		screen.blit(characterArt, getX() + (WIDTH - characterArt.w) / 2, getY() + 8);
		Font.defaultFont().draw(screen, label, getX() + WIDTH / 2, getY() + HEIGHT - 12, Font.Align.CENTERED);
		if (ClickableComponent.DEBUG_FOCUS && focused) {
			Font.defaultFont().draw(screen, "*", getX(), getY());
		}
	}
}