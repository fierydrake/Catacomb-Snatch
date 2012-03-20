package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.gameinput.LogicalInputs.LogicalInput;
import com.mojang.mojam.network.NetworkCommand;

public class ChangeLogicalInputCommand extends NetworkCommand {

	private String name;
	private boolean wasPressed = false;
	private boolean wasReleased = false;
	private boolean isDown = false;

	public ChangeLogicalInputCommand() {
	}

	public ChangeLogicalInputCommand(LogicalInput input) {
		name = input.name;
		wasPressed = input.wasPressed;
		wasReleased = input.wasReleased;
		isDown = input.isDown;
	}

	@Override
	public void read(DataInputStream dis) throws IOException {
		name = dis.readUTF();
		wasPressed = dis.readBoolean();
		wasReleased = dis.readBoolean();
		isDown = dis.readBoolean();
	}

	@Override
	public void write(DataOutputStream dos) throws IOException {
		dos.writeUTF(name);
		dos.writeBoolean(wasPressed);
		dos.writeBoolean(wasReleased);
		dos.writeBoolean(isDown);
	}

	public String getLogicalInputName() {
		return name;
	}
	
	public void copyInto(LogicalInput logicalInput) {
		if (!logicalInput.name.equals(name)) throw new IllegalArgumentException("Logical input name does not match");
		logicalInput.wasPressed = wasPressed;
		logicalInput.wasReleased = wasReleased;
		logicalInput.isDown = isDown;
	}
}
