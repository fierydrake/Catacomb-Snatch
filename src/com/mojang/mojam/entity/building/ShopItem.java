package com.mojang.mojam.entity.building;

import static com.mojang.mojam.CatacombSnatch.game;

import com.mojang.mojam.Options;
import com.mojang.mojam.entity.Entity;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.Notifications;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

/**
 * Generic shop item, available from the players base
 */
public abstract class ShopItem extends Building {
	
	private final String name;
	private Bitmap image;
    private final int cost;
    private int effectiveCost;
   

    public ShopItem(String name, double x, double y, Team team, int cost, int yOffset) {
        super(x, y, team);
        this.name = name;
        //Set building cost depending if creative mode is on or not
    	this.cost = (Options.getAsBoolean(Options.CREATIVE)) ? 0:cost;
    	yOffs = yOffset;
    	image = null;
        isImmortal = true;
    }


    @Override
    public void render(Screen screen, GameView view) {
        super.render(screen, view);
        if (team == view.getPlayer().team){ 
        	//Render the Cost text
            Font.defaultFont().draw(screen, Texts.current().cost(effectiveCost),
            		(int) (pos.x), (int) (pos.y + 10), Font.Align.CENTERED);
        }
        renderInfo(screen, view);
    }
    
	/**
	 * Render the shop info text onto the given screen
	 * 
	 * @param screen
	 *            Screen
	 */
	protected void renderInfo(Screen screen, GameView view) {
		// Draw iiAtlas' shop item info graphics, thanks whoever re-wrote this!
		if (highlightedBy.contains(view.getPlayer())) {
		        Bitmap image = getSprite();
		        int teamYOffset = (team == Team.Team2) ? 90 : 0;
		        
		        String[] tooltip = this.getTooltip();
		        int width = getLongestWidth(tooltip, Font.FONT_WHITE_SMALL)+4;
		        int height = tooltip.length*(Font.FONT_GOLD_SMALL.getFontHeight()+3);
		        
		        Font font = Font.FONT_GOLD_SMALL;
		        screen.blit(Bitmap.tooltipBitmap(width, height),
                        (int)(pos.x - image.w / 2 - 10),
                        (int)(pos.y + 20 - teamYOffset), width, height);

		        for (int i=0; i<tooltip.length; i++) {
		            font.draw(screen, tooltip[i], (int)(pos.x - image.w + 8), (int)pos.y + 22 - teamYOffset + (i==0?0:1) + i*(font.getFontHeight()+2));
		            font = Font.FONT_WHITE_SMALL;
		        }
		}
	}
    
    private String[] getTooltip() {
        return Texts.current().shopTooltipLines(name);
    }
	
	private int getLongestWidth(String[] string, Font font) {
		int res = 0;
		for ( String s : string ) {
			int w = font.calculateStringWidth(s.trim());
			res = w > res ? w : res;
		}
		return res;
	}

    @Override
    public void init() {
        effectiveCost = game().difficulty.calculateCosts(cost);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public Bitmap getSprite() {
    	return image;
    }
    
    public void setSprite(Bitmap shopItemImage) {
    	image = shopItemImage;
    }

    /**
     * Action to take when when item is used. 
     * For most cases use useAction instead.
     */
    @Override
    public void use(Entity user) {
        if (user instanceof Player && ((Player) user).getTeam() == team) {
            Player player = (Player) user;
            if (!player.isCarrying() && player.getScore() >= effectiveCost) {
            	player.payCost(effectiveCost);
            	useAction(player);
            	
            } else if (player.getScore() < effectiveCost) {
        		 Notifications.getInstance().add(
        				 Texts.current().upgradeNotEnoughMoney(effectiveCost),
        				 player.getCharacter(), player.team,
        				 Notifications.For.CHARACTER);
            }
        }
    }
    
    /**
     * Action to take when the user uses the ShopItem after 
     * cost has been deducted. Should be used in most cases Override 
     * use() if greater flexibility is needed
     */
    abstract void useAction(Player player);
    
    @Override
    public boolean upgrade(Player p) {
    	Notifications.getInstance().add(
    			Texts.current().getStatic("upgrade.shopItem"),
    			p.getCharacter(), p.team,
    			Notifications.For.CHARACTER);
        return false;
    }   

}
