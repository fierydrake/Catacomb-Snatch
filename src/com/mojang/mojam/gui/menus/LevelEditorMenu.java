package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.building.SpawnerForBat;
import com.mojang.mojam.entity.building.SpawnerForMummy;
import com.mojang.mojam.entity.building.SpawnerForScarab;
import com.mojang.mojam.entity.building.SpawnerForSnake;
import com.mojang.mojam.entity.building.TreasurePile;
import com.mojang.mojam.entity.building.Turret;
import com.mojang.mojam.entity.building.TurretTeamOne;
import com.mojang.mojam.entity.building.TurretTeamTwo;
import com.mojang.mojam.entity.mob.Bat;
import com.mojang.mojam.entity.mob.Mummy;
import com.mojang.mojam.entity.mob.Pharao;
import com.mojang.mojam.entity.mob.Scarab;
import com.mojang.mojam.entity.mob.Snake;
import com.mojang.mojam.entity.mob.SpikeTrap;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameinput.PhysicalInputs.PhysicalInputEvent;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.LevelEditorButton;
import com.mojang.mojam.gui.components.Panel;
import com.mojang.mojam.gui.components.Text;
import com.mojang.mojam.level.IEditable;
import com.mojang.mojam.level.LevelInformation;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.level.LevelUtils;
import com.mojang.mojam.level.tile.DestroyableWallTile;
import com.mojang.mojam.level.tile.FloorTile;
import com.mojang.mojam.level.tile.HoleTile;
import com.mojang.mojam.level.tile.SandTile;
import com.mojang.mojam.level.tile.UnbreakableRailTile;
import com.mojang.mojam.level.tile.UnpassableSandTile;
import com.mojang.mojam.level.tile.WallTile;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class LevelEditorMenu extends GuiMenu {

    private final int LEVEL_WIDTH = 48;
    private final int LEVEL_HEIGHT = 48;
    private final int TILE_WIDTH = 32;
    private final int TILE_HEIGHT = 32;
    private final int MENU_WIDTH = 142;
    
    private final int mapW = LEVEL_WIDTH * TILE_WIDTH;
    private final int mapH = LEVEL_HEIGHT * TILE_HEIGHT;
    private int mapX = MENU_WIDTH;
    private int mapY;
    
    private int[][] mapTile = new int[LEVEL_HEIGHT][LEVEL_WIDTH];
    private Bitmap[][] map = new Bitmap[LEVEL_HEIGHT][LEVEL_WIDTH];
    private Bitmap mapFloor = new Bitmap(mapW, mapH);
    private Bitmap minimap = new Bitmap(LEVEL_WIDTH, LEVEL_HEIGHT);
        
    private Bitmap pencil = new Bitmap(TILE_WIDTH, TILE_HEIGHT);
    private int pencilX;
    private int pencilY;
    private boolean drawing;
    
    private final IEditable[] editableTiles = {
        new FloorTile(),
        new HoleTile(),
        new SandTile(),
        new UnpassableSandTile(),
        new WallTile(),
        new DestroyableWallTile(),
        new TreasurePile(0, 0),
        new UnbreakableRailTile(new FloorTile()),
        new Turret(0, 0, Team.Neutral),
        new TurretTeamOne(0, 0),
        new TurretTeamTwo(0, 0),
        new SpikeTrap(0, 0),
        new SpawnerForBat(0, 0),
        new SpawnerForSnake(0, 0),
        new SpawnerForMummy(0, 0),
        new SpawnerForScarab(0, 0),
        new Bat(0,0),
        new Snake(0,0),
        new Mummy(0,0),
        new Scarab(0,0),
        new Pharao(0,0)
    };
    
    private final int buttonsPerPage = 12;
    private final int totalPages = (int) Math.ceil(editableTiles.length / (float) buttonsPerPage);
    private int currentPage = 0;
    private final int buttonsCols = 3;
    private final int buttonMargin = 1;
    private final int buttonsX = 7;
    private final int buttonsY = 20;
    
    private LevelEditorButton[] tileButtons;
    private LevelEditorButton selectedButton;
    
    private Button cancelButton;
    private Button confirmeSaveButton;
    private Button cancelSaveButton;
    
    private Panel savePanel;
    private ClickableComponent editorComponent;
    private Text levelName;
    
    private boolean updateButtons;
    private boolean updateTileButtons;
    private boolean saveMenuVisible;
    
    private List<LevelInformation> levels;
    private int selectedLevel;
    
    private StringBuilder saveLevelName = new StringBuilder();
    private Random random = new Random();
    
    public LevelEditorMenu() {
        super();
    	
        createGUI();
        setCurrentPage(0);
        
        // setup pencil
        pencil.fill(0, 0, pencil.w, pencil.h, 0xffcfac02);
        pencil.fill(1, 1, pencil.w - 2, pencil.h - 2, 0);

        // setup map
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                mapFloor.blit(Art.floorTiles[random.nextInt(3)][0], TILE_WIDTH * x, TILE_HEIGHT * y);
            }
        }

        // load levels list
        updateLevels();
        
        // loads first level on the list

        try {
		    openLevel(levels.get(selectedLevel));
		} catch (IOException e) {
			// FIXME Should show a user facing error (GuiError?)
			System.err.println("Sorry, could not open level");
			e.printStackTrace();
		}
    }
    
    @Override
    public void tick(LocalGameInput input) {
    	int selectTileButtonIdAfterUpdate = 0;

    	PhysicalInputs inputs = input.getCurrentPhysicalState();
    	
    	if (saveMenuVisible) {
    		// Deal with save menu
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ENTER)) {
    			confirmeSaveButton.postClick();
    		} else if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
    			cancelSaveButton.postClick();
    		} else if (inputs.wasKeyPressedConsume(KeyEvent.VK_BACK_SPACE)) {
    			saveLevelName.setLength(Math.max(0, saveLevelName.length() - 1));
    		} else {
    			PhysicalInputEvent e = inputs.consumeKeyTypedEvent();
    			if (e != null) {
    				saveLevelName.append(e.getInputChar());
    			}
    		}
    	} else {
    		// Exit
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
    			cancelButton.postClick();
    		}

    		// Start/stop/toggle drawing
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_SPACE)) {
    			drawing = true;
    		}
    		if (inputs.wasKeyReleasedConsume(KeyEvent.VK_SPACE)) {
    			drawing = false;
    		}
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_SHIFT)) {
    			drawing = !drawing;
    		}

    		// Move level with keys
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
    			mapX += 32;
    		}
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
    			mapX -= 32;
    		}
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_UP, KeyEvent.VK_W)) {
    			mapY += 32;
    		}
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_DOWN, KeyEvent.VK_S)) {
    			mapY -= 32;
    		}

    		// Tab to scroll through tiles
    		int shiftTileSelection = 0;
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_PAGE_DOWN)) shiftTileSelection = 1;
    		if (inputs.wasKeyPressedConsume(KeyEvent.VK_PAGE_UP))   shiftTileSelection = -1;
    		if (shiftTileSelection != 0) {
    			int shiftToButtonId = Mth.clamp(selectedButton.getId() + shiftTileSelection, 0, editableTiles.length - 1);
    			if (selectedButton.getId() / buttonsPerPage != shiftToButtonId / buttonsPerPage) {
    				setCurrentPage(shiftToButtonId / buttonsPerPage);
    				selectTileButtonIdAfterUpdate = shiftToButtonId % buttonsPerPage;
    			} else {
    				tileButtons[shiftToButtonId % buttonsPerPage].postClick();
    			}
    		}
    	}

        super.tick(input);
        
        // show/hide save menu buttons
        if (updateButtons) {
            updateSaveButtons();
            updateButtons = false;
        }
        
        // update tile buttons
        if (updateTileButtons){
            updateTileButtons();
            tileButtons[selectTileButtonIdAfterUpdate].postClick();
            updateTileButtons = false;
        }
        
        // lock buttons when save menu is visible
        if(saveMenuVisible) return;

        // update pencil location
        pencilX = input.getMousePosition().x - (TILE_WIDTH / 2); // TODO Check mouse scaling is right
        pencilY = input.getMousePosition().y - (TILE_HEIGHT / 2); // TODO Check mouse scaling is right

        // move level x with mouse
        if (input.getMousePosition().x - MENU_WIDTH > MENU_WIDTH) { // TODO Check mouse scaling is right
            if (pencilX + TILE_WIDTH > GameView.WIDTH
                    && -(mapX - MENU_WIDTH) < mapW - (GameView.WIDTH - MENU_WIDTH) + TILE_HEIGHT) {
                mapX -= TILE_WIDTH / 2;
            } else if (pencilX < MENU_WIDTH && mapX < MENU_WIDTH + 32) {
                mapX += TILE_WIDTH / 2;
            }
        }
        
        // move level y with mouse
        if (pencilY + TILE_HEIGHT > GameView.HEIGHT
                && -mapY < mapH - GameView.HEIGHT + TILE_HEIGHT) {
            mapY -= TILE_HEIGHT / 2;
        } else if (pencilY < 0 && mapY < TILE_HEIGHT) {
            mapY += TILE_HEIGHT / 2;
        }
               
        // draw
        if (drawing || editorComponent.isPressed()) {
            int x = (((pencilX + TILE_WIDTH / 2) - mapX) / TILE_WIDTH);
            int y = (((pencilY + TILE_HEIGHT / 2) - mapY) / TILE_HEIGHT);
            draw(selectedButton.getTile(), x, y);
        }
    }

    @Override
    public void render(Screen screen) {
        screen.clear(0);

        // level floor
        screen.blit(mapFloor, mapX, mapY);

        // level tiles
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {

                if (map[x][y] == null) continue;
   
                Bitmap tile = map[x][y];

                // change tiles that requires some sort of drawing modification
                switch (mapTile[x][y]) {
                    case HoleTile.COLOR:
                        if (y > 0 && (mapTile[x][y - 1] == HoleTile.COLOR)) {
                            tile = null;
                        } else if (y > 0 && (mapTile[x][y - 1] == SandTile.COLOR || mapTile[x][y - 1] == UnpassableSandTile.COLOR)) {
                            tile = Art.floorTiles[7][0];
                        }
                        break;
                    case UnbreakableRailTile.COLOR:
                        boolean n = y > 0 && mapTile[x][y - 1] == UnbreakableRailTile.COLOR;
                        boolean s = y < 47 && mapTile[x][y + 1] == UnbreakableRailTile.COLOR;
                        boolean w = x > 0 && mapTile[x - 1][y] == UnbreakableRailTile.COLOR;
                        boolean e = x < 47 && mapTile[x + 1][y] == UnbreakableRailTile.COLOR;

                        int c = (n ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0) + (e ? 1 : 0);
                        int img;

                        if (c <= 1) {
                            img = (n || s) ? 1 : 0;     // default is horizontal
                        } else if (c == 2) {
                            if (n && s) {
                                img = 1;                // vertical
                            } else if (w && e) {
                                img = 0;                // horizontal
                            } else {
                                img = n ? 4 : 2;        // north turn
                                img += e ? 0 : 1;       // south turn
                            }
                        } else {                        // 3 or more turning disk
                            img = 6;
                        }

                        map[x][y] = Art.rails[img][0];
                        break;
                }
                  
                // draw the tile or fill with black if it's null
                if (tile != null) {
                    screen.blit(tile,
                            x * TILE_WIDTH - (tile.w - TILE_WIDTH) / 2 + mapX,
                            y * TILE_HEIGHT - (tile.h - TILE_HEIGHT) + mapY);
                } else {
                    screen.fill(x * TILE_WIDTH + mapX, y * TILE_HEIGHT + mapY, TILE_WIDTH, TILE_HEIGHT, 0);
                }
            }
        }
        
        // pencil position indicator
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                if (x == (((pencilX + TILE_WIDTH / 2) - mapX) / TILE_WIDTH) && y == (((pencilY + TILE_HEIGHT / 2) - mapY) / TILE_HEIGHT)) {
                    screen.blit(pencil, TILE_HEIGHT * x + mapX, TILE_HEIGHT * y + mapY);
                    break;
                }
            }
        }

        super.render(screen);
        
        // minimap
        screen.blit(minimap, screen.w - minimap.w - 6, 6);
        
        // selected tile name
        Font.defaultFont().draw(screen, selectedButton != null ? selectedButton.getTile().getName() : "",
        		MENU_WIDTH / 2, 13, Font.Align.CENTERED);
        
        // current page and total pages
        Font.defaultFont().draw(screen, (currentPage + 1) + "/" + totalPages,
        		MENU_WIDTH / 2, 261, Font.Align.CENTERED);
    }
       
    private void updateTileButtons() {
        int y = 0;

        // Remove previous buttons
        if (tileButtons != null) {
            for (int i = 0; i < tileButtons.length; i++) {
                if (tileButtons[i] != null) {
                    removeButton(tileButtons[i]);
                }
            }
        }

        tileButtons = new LevelEditorButton[Math.min(buttonsPerPage,
                editableTiles.length - currentPage * buttonsPerPage)];

        for (int i = currentPage * buttonsPerPage;
                i < Math.min((currentPage + 1) * buttonsPerPage, editableTiles.length); i++) {
            int x = i % buttonsCols;
            int id = i % buttonsPerPage;

            tileButtons[id] = (LevelEditorButton) addButton(new LevelEditorButton(i, editableTiles[i],
                    buttonsX + x * (LevelEditorButton.WIDTH + buttonMargin), buttonsY + y));

            if (id == 0) {
                selectedButton = tileButtons[id];
                selectedButton.setActive(true);
            }

            if (x == (buttonsCols - 1)) {
                y += LevelEditorButton.HEIGHT + buttonMargin;
            }
        }
    }
    
    private boolean hasPreviousPage() {
	    return currentPage > 0;
	}
	
    private boolean hasNextPage() {
        return (currentPage + 1) * buttonsPerPage < editableTiles.length;
    }
    
    private void setCurrentPage(int page) {
        currentPage = page;
        updateTileButtons = true;
    }
    
    private void updateSaveButtons() {
        if (saveMenuVisible) {
            addButton(savePanel);
            addButton(confirmeSaveButton);
            addButton(cancelSaveButton);
        } else {
            removeButton(confirmeSaveButton);
            removeButton(cancelSaveButton);
            removeButton(savePanel);
        }
    }

    private void updateLevels() {
        LevelList.resetLevels();
        levels = LevelList.getLevels();
    }

    private void draw(IEditable tileOrEntity, int x, int y) {

        if (x < 0 || x > LEVEL_WIDTH - 1) return;
        if (y < 0 || y > LEVEL_HEIGHT - 1) return;
        if (mapTile[x][y] == tileOrEntity.getColor()) return;
        
        if (tileOrEntity.getColor() != FloorTile.COLOR) {
            map[x][y] = tileOrEntity.getBitMapForEditor();
        } else {
            map[x][y] = null;
        }
        
        mapTile[x][y] = tileOrEntity.getColor();
        minimap.fill(x, y, 1, 1, tileOrEntity.getMiniMapColor() );
    }

    private void newLevel() {
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                mapTile[x][y] = FloorTile.COLOR;
                map[x][y] = null;
            }
        }
       minimap.fill(0, 0, minimap.w, minimap.h, editableTiles[0].getMiniMapColor());
       removeText(levelName);
       levelName = new Text(1, "<New Level>", 120, 5);
       addText(levelName);
    }

    private void openLevel(LevelInformation li) throws IOException {
    	newLevel();

		BufferedImage bufferedImage = ImageIO.read(li.getURL());
		int w = bufferedImage.getWidth();
		int h = bufferedImage.getHeight();
		
		int[] rgbs = new int[w * h];
		
		bufferedImage.getRGB(0, 0, w, h, rgbs, 0, w);

		removeText(levelName);
		levelName = new Text(1, li.levelName, 120, 5);
		addText(levelName);

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int col = rgbs[x + y * w] & 0xffffffff;

				IEditable tile = LevelUtils.getNewTileFromColor(col);
				draw(tile, x, y);

				if (tile instanceof FloorTile) {
					Entity entity = LevelUtils.getNewEntityFromColor(col, x, y);
					if (entity instanceof IEditable) {
						draw((IEditable) entity, x, y);
					}
				}
			}
		}
    }
    
    private boolean saveLevel(String name) {

        BufferedImage image = new BufferedImage(LEVEL_WIDTH, LEVEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < LEVEL_HEIGHT; x++) {
            for (int y = 0; y < LEVEL_WIDTH; y++) {
                image.setRGB(x, y, mapTile[x][y]);
            }
        }

        try {
            File newLevel = new File(LevelList.getBaseDir(), name + ".bmp");
            newLevel.createNewFile();
            ImageIO.write(image, "BMP", newLevel);
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException : " + ex);
            return true;
        } catch (IOException ioe) {
            System.out.println("IOException : " + ioe);
            return false;
        }

        return true;
    }
    
    private void createGUI() {
        
        levelName = new Text(1,"", 120, 5);
        
        // map clickable component
        editorComponent = addButton(new ClickableComponent(MENU_WIDTH, 0, GameView.WIDTH - MENU_WIDTH, GameView.HEIGHT) {

            @Override
            protected void clicked() {
                // do nothing, handled by button listeners
            }
        });
        
        // menu panel
        addButton(new Panel(0, 0, MENU_WIDTH, GameView.HEIGHT));
        
        // minimap panel
        addButton(new Panel(GameView.WIDTH - minimap.w - 11, 1, minimap.w + 10, minimap.w + 10));
        
        // save menu panel
        savePanel = new Panel(180, 120, 298, 105) {
            @Override
            public void render(Screen screen) {
                super.render(screen);
                Font.defaultFont().draw(screen, Texts.current().getStatic("leveleditor.enterLevelName"),
                        getX() + getWidth() / 2, getY() + 20, Font.Align.CENTERED);
                Font.defaultFont().draw(screen, saveLevelName + "_",
                        getX() + getWidth() / 2, getY() + 40, Font.Align.CENTERED);
            }
        };

        // save menu buttons
        confirmeSaveButton = new Button("leveleditor.save", 195, 190) {
        	@Override 
        	public void clicked() {
        		if (saveLevel(saveLevelName.toString())) {
        			removeText(levelName);
        			levelName = new Text(1, "+ " + saveLevelName, 120, 5);
        			addText(levelName);

        			updateLevels();

                    saveMenuVisible = false;
                    updateButtons = true;
                    saveLevelName = new StringBuilder();
        		}
        	}
        };
        cancelSaveButton = new Button("cancel", 335, 190) {
        	@Override 
        	public void clicked() {
        		saveMenuVisible = false;
        		updateButtons = true;
        		saveLevelName = new StringBuilder();
        	}
        };

        // actions buttons
        int startY = (GameView.HEIGHT - 5) - 26 * 5;
        addButton(new Button("(", 7, startY, 30, Button.HEIGHT, false) {
        	@Override 
        	public void clicked() {
        		if (hasPreviousPage()) setCurrentPage(currentPage - 1);
        	}
        });
        addButton(new Button(")", MENU_WIDTH - 37, startY, 30, Button.HEIGHT, false) {
        	@Override 
        	public void clicked() {
        		if (hasNextPage()) setCurrentPage(currentPage + 1);
        	}
        });
        addButton(new Button("leveleditor.new", 7, startY += 26) {
        	@Override 
        	public void clicked() {
                newLevel();
        	}
        });
        addButton(new Button("leveleditor.open", 7, startY += 26) {
        	@Override 
        	public void clicked() {
                selectedLevel = (selectedLevel < levels.size() - 1 ? selectedLevel + 1 : 0);
                try {
                	openLevel(LevelList.getLevels().get(selectedLevel));
                } catch (IOException e) {
                	// FIXME Should show a user facing error (GuiError?)
                	System.err.println("Sorry, could not open level");
                	e.printStackTrace();
                }
        	}
        });
        addButton(new Button("leveleditor.save", 7, startY += 26) {
        	@Override 
        	public void clicked() {
                saveMenuVisible = true;
                updateButtons = true;
        	}
        });
        cancelButton = (Button) addButton(new BackButton(7, startY += 26));
    }
    
    @Override
    public void buttonPressed(ClickableComponent button) {
        // tile buttons
        if (button instanceof LevelEditorButton) {
            LevelEditorButton lb = (LevelEditorButton) button;

            if (selectedButton != null && selectedButton != lb) {
                selectedButton.setActive(false);
                selectedButton = lb;
            }
        }
    }
}