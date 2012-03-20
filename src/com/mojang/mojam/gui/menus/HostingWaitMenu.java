package com.mojang.mojam.gui.menus;

import static com.mojang.mojam.CatacombSnatch.game;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.Options;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gui.ButtonAdapter;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.network.NetworkPacketLink;
import com.mojang.mojam.network.SyncNetworkInformation;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HostingWaitMenu extends GuiMenu {

	public String myIpLAN;
	public String myIpWAN;
	private Button cancelButton;
	
	private volatile NetworkPacketLink packetLink;

	public HostingWaitMenu() {
		super();

		Runnable waitForClientToConnect = new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(Options.getAsInteger(Options.MP_PORT, CatacombSnatch.DEFAULT_PORT));
					
					try {
						serverSocket.setSoTimeout(1000);

						Socket socket = null;
						while (socket == null && !Thread.interrupted()) {
							try {
								socket = serverSocket.accept();
								socket.setTcpNoDelay(true);
							} catch (SocketTimeoutException e) {}
						}
						if (socket != null) {
							packetLink = new NetworkPacketLink(socket);
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try { serverSocket.close(); } catch (IOException e) {}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		final Thread waitThread = new Thread(waitForClientToConnect, "Waiting for client to connect");

		cancelButton = (Button) addButton(new BackButton("cancel", 364, 335));
		cancelButton.addListener(new ButtonAdapter() {
			@Override
			public void buttonPressed(ClickableComponent button) {
				waitThread.interrupt();
			}
		});

		searchIpLAN();
		searchIpWAN();
		
		waitThread.start();
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.emptyBackground, 0, 0);
		Font font = Font.defaultFont();
		font.draw(screen, Texts.current().getStatic("mp.waitingForClient"), 100, 100);
		font.draw(screen, Texts.current().getStatic("mp.localIP") + myIpLAN, 100, 120);
		font.draw(screen, Texts.current().getStatic("mp.externalIP") + myIpWAN, 100, 140);

		super.render(screen);
	}

	@Override
	public void buttonPressed(ClickableComponent button) {
		// nothing.
	}

	@Override
	public void tick(LocalGameInput input) {
		if (packetLink != null) {
			game().networkInformation = new SyncNetworkInformation(packetLink, true);
			CatacombSnatch.startGame();
		} 
		if (input.getCurrentPhysicalState().wasKeyPressedConsume(KeyEvent.VK_ESCAPE)) {
			cancelButton.postClick();
		}
		super.tick(input);
	}

	public void searchIpWAN() {
		URL whatismyip;
		try {
			whatismyip = new URL("http://automation.whatismyip.com/n09230945.asp");
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
				myIpWAN = in.readLine();
			} catch (IOException e) {
				myIpWAN = "N/A";
			}
		} catch (MalformedURLException e) {
			myIpWAN = "N/A";
		}
	}

	public void searchIpLAN() {
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			myIpLAN = thisIp.getHostAddress();
		} catch (Exception e) {
			myIpLAN = "N/A";
		}
	}

}
