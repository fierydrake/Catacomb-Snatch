package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;

public class ShopItemHarvester extends ShopItem {

    public ShopItemHarvester(double x, double y, Team team) {
        super("harvester",x, y, team, 300, 22);
        
        int facing = (team == Team.Team2) ? 0:4;
        
        setSprite(Art.harvester[facing][0]);
    }

    public void useAction(Player player) {
        Building item = new Harvester(pos.x, pos.y, team);
        level.addEntity(item);
        player.pickup(item);
	}
}
