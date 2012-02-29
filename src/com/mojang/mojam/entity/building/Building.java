package com.mojang.mojam.entity.building;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.IUsable;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.math.BB;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Generic building class
 */
public class Building extends Mob implements IUsable {
	public static final int SPAWN_INTERVAL = 60;
	public static final int MIN_BUILDING_DISTANCE = 1700; // Sqr

	public int REGEN_INTERVAL = 15;
	public float REGEN_AMOUNT = 1;
	public boolean REGEN_HEALTH = true;
	public int healingTime = REGEN_INTERVAL;
	

	public int spawnTime = 0;
	public boolean highlight = false;
	public Mob carriedBy = null;

	protected int upgradeLevel = 0;
	private int maxUpgradeLevel = 0;
	private int[] upgradeCosts = null;

	/**
	 * Constructor
	 * 
	 * @param x
	 *            Initial X coordinate
	 * @param y
	 *            Initial Y coordinate
	 * @param team
	 *            Team number
	 */
	public Building(double x, double y, int team) {
		super(x, y, team);

		setStartHealth(20);
		freezeTime = 10;
		spawnTime = TurnSynchronizer.synchedRandom.nextInt(SPAWN_INTERVAL);
	}

	@Override
	public void render(Screen screen) {
		super.render(screen);
		renderMarker(screen);
		if (beingCapturedBy != null) {
		    renderCaptureBar(screen);
		}
	}

	// TODO: Render something nicer and with cleaner code
	private static Bitmap capturingBitmap;
	static {
	    Font f = Font.FONT_GOLD_SMALL;
	    Bitmap text = new Bitmap(f.calculateStringWidth("Capturing")+2, f.getFontHeight()+3);
        f.draw(text, "Capturing", 1, 1, 99999);
        capturingBitmap = text.outline(0xFF000055);
	}
    protected void renderCaptureBar(Screen screen) {
        BB bb = getBB();
        int h = 5, w = 40, fw = (w * captureTicks) / TICKS_TO_CAPTURE;
        int x = (int)((bb.x1+bb.x0-w)/2.0), y = (int)bb.y1-38;
        
        // Render bar
        screen.rectangle(x, y, w, h, 0xFF000055);
        screen.opacityFill(x+1, y+1, w-2, h-2, 0xFFFFFFFF, 128);
        screen.fill(x+1, y+1, fw-2, h-2, 0xFFDDFFDD);
        screen.fill(x+1, y+1, fw-3, h-2, 0xFF00FF00);
        
        // Render text
        screen.blit(capturingBitmap, (int)(Math.round(bb.x1+bb.x0-capturingBitmap.w)/2.0), (int)(bb.y1-37-capturingBitmap.h));
    }

    /**
	 * Render the marker onto the given screen
	 * 
	 * @param screen
	 *            Screen
	 */
	protected void renderMarker(Screen screen) {
		if (highlight && !isCarried()) {
			BB bb = getBB();
			bb = bb.grow((getSprite().w - (bb.x1 - bb.x0))
					/ (3 + Math.sin(System.currentTimeMillis() * .01)));
			int width = (int) (bb.x1 - bb.x0);
			int height = (int) (bb.y1 - bb.y0);
			Bitmap marker = new Bitmap(width, height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if ((x < 2 || x > width - 3 || y < 2 || y > height - 3)
							&& (x < 5 || x > width - 6)
							&& (y < 5 || y > height - 6)) {
						int i = x + y * width;
						marker.pixels[i] = 0xffffffff;
					}
				}
			}
			screen.blit(marker, bb.x0, bb.y0 - 4);
		}
	}

	@Override
	public void tick() {
		super.tick();
		
		xd = 0.0;
		yd = 0.0;
	}
	
	public void doRegenTime() {
		if (freezeTime <= 0) {
			super.doRegenTime();
		} else {
			// DO NOTHING
		}
	}
	
	/**
	 * Called if this building is picked up
	 * 
	 * @param mob Reference to the mob object carrying this building
	 */
	public void onPickup(Mob mob) {
	    carriedBy = mob;
	}
	
	/**
	 * Called if this building is dropped by its carrier
	 */
	public void onDrop() {
	    carriedBy = null;
	}
	
	/**
	 * Check if this building is being carried
	 * 
	 * @return True if carried, false if not
	 */
	public boolean isCarried() {
	    return carriedBy != null;
	}


	@Override
	public Bitmap getSprite() {
		return Art.floorTiles[3][2];
	}

	@Override
	public boolean move(double xBump, double yBump) {
		return false;
	}

	@Override
	public void slideMove(double xa, double ya) {
		super.move(xa, ya);
		fallDownHole();
	}
	
	/**
	 * Called if building upgrade is complete
	 */
	protected void upgradeComplete() {
	}

	@Override
	public boolean upgrade(Player p) {
		if (upgradeLevel >= maxUpgradeLevel) {
			MojamComponent.soundPlayer.playSound("/sound/Fail.wav",
					(float) pos.x, (float) pos.y, true);
			if (this.team == MojamComponent.localTeam) {
				Notifications.getInstance().add(
						MojamComponent.texts.getStatic("upgrade.full"));
			}
			return false;
		}

		final int cost = upgradeCosts[upgradeLevel];
		if (cost > p.getScore() && !Options.getAsBoolean(Options.CREATIVE)) {
			MojamComponent.soundPlayer.playSound("/sound/Fail.wav",
					(float) pos.x, (float) pos.y, true);
			if (this.team == MojamComponent.localTeam) {
				Notifications.getInstance().add(
						MojamComponent.texts.upgradeNotEnoughMoney(cost));
			}
			return false;
		}

		MojamComponent.soundPlayer.playSound("/sound/Upgrade.wav",
				(float) pos.x, (float) pos.y, true);

		++upgradeLevel;
		p.useMoney(cost);
		upgradeComplete();

		if (this.team == MojamComponent.localTeam) {
			Notifications.getInstance().add(
					MojamComponent.texts.upgradeTo(upgradeLevel + 1));
		}
		return true;
	}

	/**
	 * Make this building upgradeable
	 * 
	 * @param costs Cost vector
	 */
	void makeUpgradeableWithCosts(int[] costs) {
		if (costs == null) {
			maxUpgradeLevel = 0;
		} else {
			upgradeCosts = costs;
			maxUpgradeLevel = costs.length - 1;
			upgradeComplete();
		}
		return;
	}

	@Override
	public void use(Entity user) {
		if (user instanceof Player) {
			((Player) user).pickup(this);
		}
	}

	@Override
	public boolean isHighlightable() {
		return true;
	}

	@Override
	public void setHighlighted(boolean hl) {
		highlight = hl;
		justDroppedTicks = 80;
	}

	@Override
	public boolean isAllowedToCancel() {
		return true;
	}

	private static final int TICKS_TO_CAPTURE = 100;
	private Player beingCapturedBy = null;
	private int captureTicks;
    public boolean isCapturing(Player player) {
        return beingCapturedBy == player;
    }

    public void captureTick() {
        if (++captureTicks >= TICKS_TO_CAPTURE) {
            team = beingCapturedBy.team;
            beingCapturedBy = null;
            if (MojamComponent.localTeam == team) {
                Notifications.getInstance().add("YOU HAVE CAPTURED AN ENEMY BUILDING!");
            }
        }
    }
    
    public void cancelCapture() {
        beingCapturedBy = null;
    }

    public void captureStart(Player player) {
        if (beingCapturedBy == null) {
            captureTicks = 1;
            beingCapturedBy = player;
        } else {
            beingCapturedBy = null;
        }
    }
}
