package com.mojang.mojam.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.gamelogic.LocalGameLogic;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.level.DifficultyInformation;
import com.mojang.mojam.level.DifficultyList;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class DifficultySelect extends GuiMenu {
	
	private static final int DEFAULT_DIFFICULTY = 1;
	
	private ArrayList<DifficultyInformation> difficulties = DifficultyList.getDifficulties();
	
	private Checkbox[] DifficultyCheckboxes;
	private final int xButtons = 3;
	private final int xSpacing = Checkbox.WIDTH + 8;
	private final int ySpacing = Checkbox.HEIGHT + 8;
	private final int xStart = (GameView.WIDTH - (xSpacing * xButtons)) / 2;
	private final int yStart = 75;
	
	private Button startGameButton;
	private Button cancelButton;

	public DifficultySelect(final boolean hosting) {
		super();
		
		DifficultyCheckboxes = new Checkbox[difficulties.size()];
		setupDifficultyButtons();
		
		logic().setDifficulty(difficulties.get(DEFAULT_DIFFICULTY));
		
		startGameButton = new Button("diffselect.start", (GameView.WIDTH - 256 - 30), 
				GameView.HEIGHT - 24 - 25);
		startGameButton.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				CatacombSnatch.startGame(hosting);
			}
		});
		cancelButton = new Button("cancel", 
				GameView.WIDTH - 128 - 20, GameView.HEIGHT - 24 - 25);
		cancelButton.addListener(menus.BACK_BUTTON_LISTENER);
		
		addButton(startGameButton);
		addButton(cancelButton);
	}
	
	private void setupDifficultyButtons() {
		int y = 0;

        for (int i = 0; i < difficulties.size(); i++) {
            int x = i % xButtons;
            
            DifficultyCheckboxes[i] = (Checkbox) addButton(new Checkbox(difficulties.get(i).difficultyName, xStart + x * xSpacing, yStart + ySpacing * y));
            
            if (i == DEFAULT_DIFFICULTY) {
                DifficultyCheckboxes[i].checked = true;
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
			for (int i=0; i<DifficultyCheckboxes.length; i++) {
				if (cb == DifficultyCheckboxes[i]) {
					logic().setDifficulty(difficulties.get(i));
					DifficultyCheckboxes[i].checked = true;
				} else {
					DifficultyCheckboxes[i].checked = false;
				}
			}
		}
	}
    
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			shiftDifficulty(-1);
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			shiftDifficulty(1);
		} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			shiftDifficulty(-xButtons);
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			shiftDifficulty(xButtons);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_E) {
			startGameButton.postClick();
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cancelButton.postClick();
		} else {
			super.keyPressed(e);
		}
	}
	
	private void shiftDifficulty(int shift) {
		for (int i=0; i<DifficultyCheckboxes.length; i++) {
			if (DifficultyCheckboxes[i].checked) {
				int newDifficultyIdx = Mth.clamp(i + shift, 0, DifficultyCheckboxes.length-1);
				buttonPressed(DifficultyCheckboxes[newDifficultyIdx]);
				break;
			}
		}
	}		
}
