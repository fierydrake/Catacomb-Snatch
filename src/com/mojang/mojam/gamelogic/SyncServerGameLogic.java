package com.mojang.mojam.gamelogic;

import java.net.Socket;

import com.mojang.mojam.GameInformation;
import com.mojang.mojam.entity.Player;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.level.Level;

public class SyncServerGameLogic implements GameLogic {
	
	private GameInformation gameInformation;
	private Socket socket;
	
	private Level level;
	
	public SyncServerGameLogic(GameInformation gameInformation, Socket socket) {
		this.gameInformation = gameInformation;
		this.socket = socket;
	}
	
	@Override
	public GameInformation getGameInformation() {
		return gameInformation;
	}

	@Override
	public Level getLevel() {
		return null;
	}

	@Override
	public Player[] getPlayers() {
		return null;
	}

	@Override
	public Player getLocalPlayer() {
		return null;
	}

	@Override
	public boolean isMouseActive() {
		return false;
	}

	@Override
	public void tick(GameInput input) {
		
	}
}
