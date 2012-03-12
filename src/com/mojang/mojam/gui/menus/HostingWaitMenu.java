package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gui.ButtonAdapter;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Screen;

public class HostingWaitMenu extends GuiMenu {

	public String myIpLAN;
	public String myIpWAN;
	private Button cancelButton;

	public HostingWaitMenu() {
		super();

		cancelButton = (Button) addButton(new Button("cancel", 364, 335));
		cancelButton.addListener(new ButtonAdapter() {}); // TODO CANCEL JOIN

		searchIpLAN();
		searchIpWAN();
	}

	@Override
	public void render(Screen screen) {

		screen.clear(0);
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
