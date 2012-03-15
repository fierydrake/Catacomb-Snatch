package com.mojang.mojam.entity.building;

import com.mojang.mojam.entity.Player;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.screen.Art;

public class ShopItemTurret extends ShopItem {

	public ShopItemTurret(double x, double y, Team team) {
        super("turret",x, y, team, 150, 10);
        
        int facing = (team == Team.Team2) ? 0:4;
        
        setSprite(Art.turret[facing][0]);
    }

	@Override
    public void useAction(Player player) {
        Building item = new Turret(pos.x, pos.y, team);
        level.addEntity(item);
        player.pickup(item);
	}
}
