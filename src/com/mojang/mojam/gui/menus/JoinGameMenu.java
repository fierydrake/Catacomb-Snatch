package com.mojang.mojam.gui.menus;

import static com.mojang.mojam.CatacombSnatch.game;
import static com.mojang.mojam.CatacombSnatch.menus;

import java.awt.event.KeyEvent;
import java.io.IOException;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.GameInformation;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameinput.PhysicalInputs.PhysicalInputEvent;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.network.ClientSidePacketLink;
import com.mojang.mojam.network.PacketLink;
import com.mojang.mojam.network.SyncNetworkInformation;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class JoinGameMenu extends GuiMenu {

	private StringBuilder ip = new StringBuilder();
	private Button joinButton;
	private Button cancelButton;

	public JoinGameMenu() {
		super();

		joinButton = (Button) addButton(new Button("mp.join", 100, 180) {
			@Override
			public void clicked() {
				String[] parts = JoinGameMenu.this.ip.toString().split(":");
				String host = parts[0];
				int port = CatacombSnatch.DEFAULT_PORT;
				if (parts.length > 1) port = Integer.parseInt(parts[1]);
				
				try {
					PacketLink packetLink = new ClientSidePacketLink(host, port);
					game().type = GameInformation.Type.SYNCHED_NETWORK;
					game().networkInformation = new SyncNetworkInformation(packetLink, false);
					CatacombSnatch.startGame();
				} catch (IOException e) {
					e.printStackTrace();
					menus().push(new GuiError("Failed to connect to "+host+":"+port));
				}
			}
		});
		cancelButton = (Button) addButton(new BackButton("cancel", 250, 180));
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		super.render(screen);
		Font.defaultFont().draw(screen, Texts.current().getStatic("mp.enterIP"), 100, 100);
		Font.defaultFont().draw(screen, ip.toString() + "-", 100, 120);
	}

	@Override
	public void tick(LocalGameInput input) {
		PhysicalInputs inputs = input.getCurrentPhysicalState();
		// Start on Enter, Cancel on Escape
		if (inputs.wasKeyPressedConsume(KeyEvent.VK_ENTER, KeyEvent.VK_E)) {
			if (ip.length() > 0) {
				joinButton.postClick();
			}
		} else if (inputs.wasKeyPressed(KeyEvent.VK_BACK_SPACE)) {
			ip.setLength(Math.max(0, ip.length() - 1));
		} else if (inputs.wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
			cancelButton.postClick();
		} else {
			PhysicalInputEvent e = inputs.consumeKeyTypedEvent();
			if (e != null) ip.append(e.getInputChar());
		}
		super.tick(input);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
	}

}
