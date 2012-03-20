package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.GameCharacter;
import com.mojang.mojam.network.NetworkCommand;

public class CharacterCommand extends NetworkCommand {

	private int playerID;
	private GameCharacter character;

	public CharacterCommand() {}

	public CharacterCommand(int playerID, GameCharacter character) {
		this.playerID = playerID;
		this.character = character;
	}

	public int getPlayerID() {
		return playerID;
	}

	public GameCharacter getCharacter() {
		return character;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		playerID = dis.readInt();
		character = GameCharacter.valueOf(dis.readUTF());
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeInt(playerID);
		dos.writeUTF(character.name());
	}

}
