package com.mojang.mojam.gamelogic;

import java.io.IOException;

import com.mojang.mojam.GameInformation;
import com.mojang.mojam.Options;
import com.mojang.mojam.PlayerInformation;
import com.mojang.mojam.entity.mob.Team;
import com.mojang.mojam.gameinput.NetworkGameInput;
import com.mojang.mojam.gameview.NetworkGameView;
import com.mojang.mojam.gameview.SimpleGameView;
import com.mojang.mojam.level.LevelList;
import com.mojang.mojam.network.Packet;
import com.mojang.mojam.network.SyncNetworkInformation;
import com.mojang.mojam.network.TurnSynchronizer;
import com.mojang.mojam.network.packet.CharacterCommand;
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.StartGamePacketCustom;

public class SyncClientGameLogic extends SyncServerGameLogic {
	public static final int PLAYER_ID = 1;
	public SyncClientGameLogic(GameInformation game) 
	throws IOException {
		this.game = game;
		this.packetLink = ((SyncNetworkInformation)game.networkInformation).packetLink;
		
		if (game.players.size() != 1) throw new IllegalArgumentException("More than 1 local player not implemented");
		
		System.err.println("Client");
		
		synchronizer = new TurnSynchronizer(this, packetLink, PLAYER_ID, 2);
		packetLink.setPacketListener(this);
	}

	@Override
	public void handle(Packet packet) {
		if (packet instanceof StartGamePacket || packet instanceof StartGamePacketCustom) {
			StartGamePacket sgPacket = (StartGamePacket) packet;
			synchronizer.onStartGamePacket(sgPacket);
			game.difficulty = GameInformation.DIFFICULTIES.get(sgPacket.getDifficulty());
			if (packet instanceof StartGamePacket) {
				game.level = LevelList.getForPath(sgPacket.getLevelFile());
				try {
					level = game.generateLevel();
				} catch (IOException e) {/* FIXME Crap out later with a nullpointerexception */ }
			} else {
				level = ((StartGamePacketCustom)sgPacket).getLevel();
			}
			/* Add hosting player at index 0 */
			addOrUpdatePlayer(0, new PlayerInformation(Team.Team1, sgPacket.getOpponentCharacter(), new NetworkGameInput(), new NetworkGameView()));
			/* Add local player at index 1 */
			addOrUpdatePlayer(PLAYER_ID, new PlayerInformation(Team.Team2, Options.getCharacter(), new NetworkGameInput(), new SimpleGameView()));
			synchronizer.addCommand(new CharacterCommand(PLAYER_ID, Options.getCharacter()));
		} else {
			super.handle(packet);
		}
	}
}
