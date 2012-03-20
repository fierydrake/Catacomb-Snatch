package com.mojang.mojam.gui.menus;

import static com.mojang.mojam.CatacombSnatch.game;
import static com.mojang.mojam.CatacombSnatch.menus;

import java.awt.event.KeyEvent;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.Checkbox;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DifficultySelect extends GuiMenu {
	
	private Checkbox[] difficultyCheckboxes;
	private final int xButtons = 3;
	private final int xSpacing = Checkbox.WIDTH + 8;
	private final int ySpacing = Checkbox.HEIGHT + 8;
	private final int xStart = (GameView.WIDTH - (xSpacing * xButtons)) / 2;
	private final int yStart = 75;
	
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect(final boolean hosting) {
		super();
		
		difficultyCheckboxes = new Checkbox[GameInformation.DIFFICULTIES.size()];
		setupDifficultyButtons();
		
		startGameButton = new Button("diffselect.start", (GameView.WIDTH - 256 - 30), GameView.HEIGHT - 24 - 25) {
			@Override
			public void clicked() {
				if (hosting) {
					menus().push(new HostingWaitMenu());
				} else {
					CatacombSnatch.startGame();
				}
			}
		};
		cancelButton = new BackButton("cancel", GameView.WIDTH - 128 - 20, GameView.HEIGHT - 24 - 25);
		
		addButton(startGameButton);
		addButton(cancelButton);
	}
	
	private void setupDifficultyButtons() {
		int y = 0;

        for (int i = 0; i < difficultyCheckboxes.length; i++) {
            int x = i % xButtons;
            
            DifficultyInformation difficulty = GameInformation.DIFFICULTIES.get(i);
            difficultyCheckboxes[i] = new Checkbox(difficulty.difficultyName, xStart + x * xSpacing, yStart + ySpacing * y);
            addButton(difficultyCheckboxes[i]);
            
            if (difficulty == game().difficulty) {
                difficultyCheckboxes[i].checked = true;
            }
        
            if (x == (xButtons - 1))
                y++;
        }
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("diffselect.title"), 20, 20);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		if (button instanceof Checkbox) {
			Checkbox cb = (Checkbox) button;
			for (int i=0; i<difficultyCheckboxes.length; i++) {
				if (cb == difficultyCheckboxes[i]) {
					game().difficulty = GameInformation.DIFFICULTIES.get(i);
					difficultyCheckboxes[i].checked = true;
				} else {
					difficultyCheckboxes[i].checked = false;
				}
			}
		}
	}
    
	@Override
	public void tick(LocalGameInput input) {
		PhysicalInputs inputs = input.getCurrentPhysicalState();
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
			shiftDifficulty(-1);
		}
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
			shiftDifficulty(1);
		}
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_UP, KeyEvent.VK_W)) {
			shiftDifficulty(-xButtons);
		}
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_DOWN, KeyEvent.VK_S)) {
			shiftDifficulty(xButtons);
		}
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ENTER, KeyEvent.VK_E)) {
			startGameButton.postClick();
		}
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
			cancelButton.postClick();
		}
		super.tick(input);
	}
	
	private void shiftDifficulty(int shift) {
		for (int i=0; i<difficultyCheckboxes.length; i++) {
			if (difficultyCheckboxes[i].checked) {
				int newDifficultyIdx = Mth.clamp(i + shift, 0, difficultyCheckboxes.length-1);
				buttonPressed(difficultyCheckboxes[newDifficultyIdx]);
				break;
			}
		}
	}		
}
