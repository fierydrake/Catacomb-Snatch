package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;

public class ShopItemBomb extends ShopItem {

    public ShopItemBomb(double x, double y, Team team) {
        super("bomb",x, y, team, 500, 7);
                
        setSprite(Art.bomb);
    }

    @Override
    public void useAction(Player player) {
        Building item = new Bomb(pos.x, pos.y);
        level.addEntity(item);
        player.pickup(item);
	}
}
