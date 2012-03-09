package com.mojang.mojam.gui;

import com.mojang.mojam.Options;
import com.mojang.mojam.gamesound.GameSound;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class AudioVideoMenu extends GuiMenu {
	private boolean fullscreen;
	private boolean fps;
	private float musicVolume;
	private float soundsVolume;
	private float volume;

	private int textY;

	private Button back;
	private Checkbox fullscreenBtn;
	private Checkbox fpsBtn;
	private Slider soundVol;
	private Slider musicVol;
	private Slider soundsVol;

	public AudioVideoMenu() {
		super();
		
		loadOptions();

		int offset = 32;
		int xOffset = (GameView.WIDTH - Button.WIDTH) / 2;
		int yOffset = (GameView.HEIGHT - (7 * offset + 20 + (offset * 2))) / 2;
		textY = yOffset;
		yOffset += offset;

		fullscreenBtn = (Checkbox) addButton(new Checkbox("options.fullscreen", xOffset, yOffset += offset, Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE)));
		fpsBtn = (Checkbox) addButton(new Checkbox("options.showfps", xOffset, yOffset += offset, Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE)));
		soundVol = (Slider) addButton(new Slider("options.volume", xOffset, yOffset += offset, volume));
		musicVol = (Slider) addButton(new Slider("options.music", xOffset - xOffset / 3 - 20, yOffset += offset, musicVolume));
		soundsVol = (Slider) addButton(new Slider("options.sounds", xOffset + xOffset / 3 + 20, yOffset, soundsVolume));

		back = (Button) addButton(new Button("back", xOffset, (yOffset += offset) + 20));
		back.addListener(menus.BACK_BUTTON_LISTENER);

		fullscreenBtn.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fullscreen = !fullscreen;
				Options.set(Options.FULLSCREEN, fullscreen);
				//toggleFullscreen(); // FIXME
			}
		});
		fpsBtn.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				fps = !fps;
				Options.set(Options.DRAW_FPS, fps);
			}
		});
		soundVol.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				volume = slider.value;

				Options.set(Options.VOLUME, volume + "");
				sound.setMasterVolume(slider.value);
			}
		});
		musicVol.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				musicVolume = slider.value;

				Options.set(Options.MUSIC, musicVolume + "");
				sound.setVolume(GameSound.BACKGROUND_TRACK, slider.value);
			}
		});
		soundsVol.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Slider slider = (Slider) button;
				soundsVolume = slider.value;

				Options.set(Options.SOUND, soundsVolume + "");
			}
		});
		back.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				Options.saveProperties();
			}
		});
	}

	private void loadOptions() {
		fullscreen = Options.getAsBoolean(Options.FULLSCREEN, Options.VALUE_FALSE);
		fps = Options.getAsBoolean(Options.DRAW_FPS, Options.VALUE_FALSE);
		musicVolume = Options.getAsFloat(Options.MUSIC, "1.0f");
		soundsVolume = Options.getAsFloat(Options.SOUND, "1.0f");
		volume = Options.getAsFloat(Options.VOLUME, "1.0f");
	}

	@Override
	public void render(Screen screen) {

		if (logic().isPlayingLevel()) {
			screen.alphaFill(0, 0, screen.w, screen.h, 0xff000000, 0x30);
		} else {
			screen.blit(Art.background, 0, 0);
		}

		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("titlemenu.sound_and_video"), screen.w / 2, textY, Font.Align.CENTERED);
		screen.blit(Art.getLocalPlayerArt()[0][6], buttons.get(selectedItem).getX() - 40, buttons.get(selectedItem).getY() - 8);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}
}
