package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.level.Level;
import com.mojang.mojam.level.TileID;

public class StartGamePacketCustom extends StartGamePacket {

	private int levelWidth, levelHeight;
	private Short[] shorts;
	private Level level;

	public StartGamePacketCustom() {}

	public StartGamePacketCustom(long gameSeed, Level level, int difficulty, GameCharacter opponentCharacter) {
		super(gameSeed, "", difficulty, opponentCharacter);
		this.level = level;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		super.read(dis);
		levelWidth = dis.readInt();
		levelHeight = dis.readInt();
		shorts = new Short[levelWidth * levelHeight];
		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = dis.readShort();
		}
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(level.width);
		dos.writeInt(level.height);
		for (int i = 0; i < level.tiles.length; i++) {
			dos.writeShort(TileID.tileToShort(level.tiles[i]));
		}
	}

	public Level getLevel() {
		if (level == null) {
			level = new Level(levelWidth, levelHeight);
			for (int x = 0; x < level.width; x++) {
				for (int y = 0; y < level.width; y++) {
					int index = x + y * level.width;
					level.setTile(x, y, TileID.shortToTile(shorts[index], level, x, y));
				}
			}
		}
		return level;
	}

}
