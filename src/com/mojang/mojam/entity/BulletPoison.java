package com.mojang.mojam.entity;

import static com.mojang.mojam.CatacombSnatch.sound;

import com.mojang.mojam.entity.building.Building;
import com.mojang.mojam.entity.mob.Mob;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class BulletPoison extends Bullet {

	public BulletPoison(Mob e, double xa, double ya, float damage) {
		super(e, xa, ya, damage);
		life=80;
		this.xa = xa * 3;
		this.ya = ya * 3;
		
	}

	@Override
	public void render(Screen screen, GameView view) {
		screen.blit(Art.bulletpoison[facing][0], pos.x - 8, pos.y - 10);
	}
	
	@Override
	public void collide(Entity entity, double xa, double ya) {
		if (entity instanceof Mob) {
			Mob mobEnt = (Mob) entity;
			if (mobEnt.isNotFriendOf(owner) && !(entity instanceof Building)) {
				mobEnt.hurt(this,damage);
				hit = true;
			}
		}
		if (hit) {
			sound().playSound("/sound/Shot 2.wav", (float) pos.x, (float) pos.y);
		}
	}
	
}
