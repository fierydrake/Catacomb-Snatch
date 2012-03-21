package com.mojang.mojam.gamelogic;

import static com.mojang.mojam.CatacombSnatch.menus;

import java.awt.Point;
import java.io.IOException;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.LatencyCache;
import com.mojang.mojam.Options;
import com.mojang.mojam.PlayerInformation;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.GameInput;
import com.mojang.mojam.gameinput.LogicalInputs.LogicalInput;
import com.mojang.mojam.gameinput.NetworkGameInput;
import com.mojang.mojam.gameview.NetworkGameView;
import com.mojang.mojam.gameview.SimpleGameView;
import com.mojang.mojam.network.CommandListener;
import com.mojang.mojam.network.NetworkCommand;
import com.mojang.mojam.network.Packet;
import com.mojang.mojam.network.PacketLink;
import com.mojang.mojam.network.PacketListener;
import com.mojang.mojam.network.SyncNetworkInformation;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.network.packet.ChangeLogicalInputCommand;
import com.mojang.mojam.network.packet.ChangeMouseCoordinateCommand;
import com.mojang.mojam.network.packet.CharacterCommand;
import com.mojang.mojam.network.packet.PingPacket;
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.SyncCheckPacket;
import com.mojang.mojam.network.packet.TurnPacket;
import com.mojang.mojam.network.packet.UnpausePacket;

public class SyncServerGameLogic extends LocalGameLogic implements CommandListener, PacketListener {
	
	public static final int PLAYER_ID = 0;
	
	protected TurnSynchronizer synchronizer;
	protected PacketLink packetLink;
	public LatencyCache latencyCache = new LatencyCache();;
	
	protected SyncServerGameLogic() {}
	
	public SyncServerGameLogic(GameInformation game) 
	throws IOException {
		this.game = game;
		this.packetLink = ((SyncNetworkInformation)game.networkInformation).packetLink;

		synchronizer = new TurnSynchronizer(this, packetLink, PLAYER_ID, 2);
		packetLink.setPacketListener(this);
		
		System.err.println("Server");
		
		level = game.generateLevel();
		addOrUpdatePlayer(PLAYER_ID, new PlayerInformation(Team.Team1, Options.getCharacter(), new NetworkGameInput(), new SimpleGameView()));
		startGame();
	}
	
	private void startGame() {
		synchronizer.setStarted(true);
		if (game.level.vanilla) {
			packetLink.sendPacket(new StartGamePacket(TurnSynchronizer.synchedSeed, game.level.getPath(), game.difficulty.difficultyID, Options.getCharacter()));
		} else  {
			throw new UnsupportedOperationException("Custom maps not supported");
//			packetLink.sendPacket(new StartCustomGamePacket(TurnSynchronizer.synchedSeed, game.level, game.difficulty.difficultyID, localPlayer.getCharacter().ordinal()));
		}
	}
	
	public void unpause() {
		packetLink.sendPacket(new UnpausePacket());
	}

	@Override
	public void handle(int playerId, NetworkCommand packet) {
		if (packet instanceof CharacterCommand) {
			CharacterCommand charCommand = (CharacterCommand) packet;
			System.err.println("Calling add player from CharacterCommand with id=" + charCommand.getPlayerID());
			addOrUpdatePlayer(charCommand.getPlayerID(), new PlayerInformation(Team.Team2, charCommand.getCharacter(), new NetworkGameInput(), new NetworkGameView()));
		}
		
		if (!(players.get(playerId).getInput() instanceof NetworkGameInput)) return;
		NetworkGameInput input = (NetworkGameInput)players.get(playerId).getInput();
		
		if (packet instanceof ChangeLogicalInputCommand) {
			ChangeLogicalInputCommand clic = (ChangeLogicalInputCommand) packet;
			input.updateLogicalInput(clic);
		}

		if (packet instanceof ChangeMouseCoordinateCommand) {
			ChangeMouseCoordinateCommand ccc = (ChangeMouseCoordinateCommand) packet;
			input.updateMouse(ccc);
		}

//		if (packet instanceof ChatCommand) {
//			ChatCommand cc = (ChatCommand) packet;
//			chat.addMessage(cc.getMessage());
//		}

	}

	@Override
	public void handle(Packet packet) {
		if (CatacombSnatch.isGamePaused() && packet instanceof UnpausePacket) {
			menus().clear();
		}
		if (packet instanceof TurnPacket) {
			synchronizer.onTurnPacket((TurnPacket) packet);
		} 
		if (packet instanceof PingPacket) {
			PingPacket pp = (PingPacket) packet;
			synchronizer.onPingPacket(pp);
			if (pp.getType() == PingPacket.TYPE_ACK) {
				latencyCache.addToLatencyCache(pp.getLatency());
			}
		}
		if (packet instanceof SyncCheckPacket) {
			SyncCheckPacket scp = (SyncCheckPacket)packet;
			synchronizer.onSyncCheckPacket(scp);
		}
	}
	
	@Override
	public void tick() {
		packetLink.tick();
		
		if (!CatacombSnatch.isGamePaused()) {
			if (synchronizer.preTurn()) {
				synchronizer.postTurn();
				
				/* Add this tick's local inputs to the command queue */
				GameInput localGameInput = CatacombSnatch.getLocalInput();
				for (LogicalInput input : localGameInput.getCurrentState().getAll()) {
					if (input.wasPressed || input.wasReleased) {
						synchronizer.addCommand(new ChangeLogicalInputCommand(input));
					}
				}
				Point mousePos = localGameInput.getMousePosition();
				synchronizer.addCommand(new ChangeMouseCoordinateCommand(mousePos.x, mousePos.y, !localGameInput.isMouseActive()));
				
				super.tick();
				localGameInput.tick();
	//			tickChat();
			}
		}
	}
}
