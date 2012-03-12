package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.LevelButton;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class LevelSelect extends GuiMenu {
    
    private final int LEVELS_PER_PAGE = 9;
    
	private List<LevelInformation> levels;

    private int currentPage = 0;
	private LevelButton[] levelButtons = null;
	
	private final int xButtons = (GameView.WIDTH / LevelButton.WIDTH);
	private final int xSpacing = LevelButton.WIDTH + 8;
	private final int ySpacing = LevelButton.HEIGHT + 8;
	private final int xStart = (GameView.WIDTH - (xSpacing * xButtons) + 8) / 2;
	private final int yStart = 50;

	private LevelButton activeButton;
	
	private Button startGameButton;
	private Button cancelButton;
    private Button previousPageButton;
    private Button nextPageButton;
    private boolean outdatedLevelButtons = false;
	
	public LevelSelect(final boolean hosting) {
		// Get all levels
		LevelList.resetLevels();
		levels = LevelList.getLevels();
		menus.getGameInformation().level = levels.get(0);

		// Add main buttons
		startGameButton = (Button) addButton(new Button("levelselect.start", GameView.WIDTH - 256 - 30, GameView.HEIGHT - 24 - 25) {
			@Override
			public void clicked() {
				menus.push(new DifficultySelect(hosting));
			}
		});
		cancelButton = (Button) addButton(new BackButton("cancel", GameView.WIDTH - 128 - 20, GameView.HEIGHT - 24 - 25) {
			@Override
			public void clicked() {
				// Interrupt host thread
				super.clicked();
			}
		});
		
		/*addButton(new Button(TitleMenu.UPDATE_LEVELS, MojamComponent.texts.getStatic("levelselect.update"), 
				MojamComponent.GAME_WIDTH - 128 - 18, 20));
		 //levels already load by default, no update needed
		*/

		// Add page buttons
		if (levels.size() > LEVELS_PER_PAGE) {
	        previousPageButton = (Button) addButton(new Button("(", 
	                xStart, GameView.HEIGHT - 24 - 25, 30, Button.HEIGHT, false));
	        nextPageButton = (Button) addButton(new Button(")", 
	                xStart + 40, GameView.HEIGHT - 24 - 25, 30, Button.HEIGHT, false));
		}
        
        // Create level
		goToPage(0);
	}

	private void goToPage(int page) {
        currentPage = page;
        outdatedLevelButtons = true;
    }

    private void updateLevelButtons() {
    	int y = 0;
    	
    	// Remove previous buttons
    	if (levelButtons != null) {
            for (int i = 0; i < levelButtons.length; i++) {
                if (levelButtons[i] != null) {
                    removeButton(levelButtons[i]);
                }
            }
    	}
    	
    	// Create level buttons
        levelButtons = new LevelButton[Math.min(LEVELS_PER_PAGE,
                levels.size() - currentPage * LEVELS_PER_PAGE)];
    	for (int i = currentPage * LEVELS_PER_PAGE;
    	         i < Math.min((currentPage + 1) * LEVELS_PER_PAGE, levels.size());
    	         i++) {
    		int x = i % xButtons;
    		int buttonIndex = i % LEVELS_PER_PAGE;
    		
    		levelButtons[buttonIndex] = new LevelButton(i, levels.get(i), xStart + x * xSpacing, yStart + ySpacing * y);
    		addButton(levelButtons[buttonIndex]);
    		if (buttonIndex == 0) {
    			activeButton = levelButtons[buttonIndex];
    			activeButton.setActive(true);
    		}
    
    		if (x == (xButtons - 1)) {
    			y++;
    		}
    	}
    }

    private boolean hasPreviousPage() {
	    return currentPage > 0;
	}
	
    private boolean hasNextPage() {
        return (currentPage + 1) * LEVELS_PER_PAGE < levels.size();
    }
	
    @Override
    public void tick(LocalGameInput input) {
        PhysicalInputs inputs = input.getCurrentPhysicalState();
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_PAGE_UP) && hasPreviousPage()) {
        	goToPage(currentPage - 1);
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_PAGE_DOWN) && hasNextPage()) {
        	goToPage(currentPage + 1);
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
        	shiftActiveButton(-1);
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
        	shiftActiveButton(1);
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_UP, KeyEvent.VK_W)) {
        	shiftActiveButton(-3);
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_DOWN, KeyEvent.VK_S)) {
        	shiftActiveButton(3);
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_ENTER, KeyEvent.VK_E)) {
        	startGameButton.postClick();
        }
        if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
        	cancelButton.postClick();
        }
        
        super.tick(input);

        if (outdatedLevelButtons) {
        	updateLevelButtons();
        	outdatedLevelButtons = false;
        }
    }
    
    private void shiftActiveButton(int shift) {
    	activeButton.setActive(false);
    	activeButton = levelButtons[Mth.clamp(activeButton.getId() + shift, 0, levelButtons.length - 1)];
    	activeButton.setActive(true);
    	activeButton.postClick();
    }
    
    @Override
    public void render(Screen screen) {
    	screen.blit(Art.emptyBackground, 0, 0);
    	
    	// Draw disabled page buttons
    	if (levels.size() > LEVELS_PER_PAGE) {
    		if (!hasPreviousPage()) {
    			previousPageButton.render(screen);
    			screen.fill(previousPageButton.getX() + 4, previousPageButton.getY() + 4,
    					previousPageButton.getWidth() - 8, previousPageButton.getHeight() - 8, 0x75401f);
    		}
    		if (!hasNextPage()) {
    			nextPageButton.render(screen);
    			screen.fill(nextPageButton.getX() + 4, nextPageButton.getY() + 4,
    					nextPageButton.getWidth() - 8, nextPageButton.getHeight() - 8, 0x75401f);
    		}
    		previousPageButton.enabled = hasPreviousPage();
    		nextPageButton.enabled = hasNextPage();
    	}
    	
    	super.render(screen);
    	Font.defaultFont().draw(screen, Texts.current().getStatic("levelselect.title"), 20, 20);
    }

    @Override
    public void buttonPressed(ClickableComponent button) {
    	
    	if (button instanceof LevelButton) {
    		
    		LevelButton lb = (LevelButton) button;
    		menus.getGameInformation().level = levels.get(lb.getId());
    		
    		if (activeButton != null && activeButton != lb) {
    			activeButton.setActive(false);
    		}
    		
    		activeButton = lb;
    	}
    	
    	else if (button == previousPageButton && hasPreviousPage()) {
    		goToPage(currentPage - 1);
    	}
    	
    	else if (button == nextPageButton && hasNextPage()) {
    		goToPage(currentPage + 1);
    	}
    }
}
