package com.mojang.mojam.network;

public class SyncNetworkInformation implements NetworkInformation {
	public PacketLink packetLink;
	public boolean hosting;
	
	public SyncNetworkInformation(PacketLink packetLink, boolean hosting) {
		this.packetLink = packetLink;
		this.hosting = hosting;
	}
	
	public boolean isHost() { return hosting; }
}
