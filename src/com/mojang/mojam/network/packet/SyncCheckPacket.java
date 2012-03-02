package com.mojang.mojam.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mojang.mojam.network.Packet;

public class SyncCheckPacket extends Packet {
    private int turn;
    private int count;

    public SyncCheckPacket() {
    }
    
    public SyncCheckPacket(int turn, int count) {
        this.turn = turn;
        this.count = count;
    }

    @Override
    public void read(DataInputStream dis) throws IOException {
        turn = dis.readInt();
        count = dis.readInt();
    }

    @Override
    public void write(DataOutputStream dos) throws IOException {
        dos.writeInt(turn);
        dos.writeInt(count);
    }

    public int getTurn() { return turn; }
    public int getCount() { return count; }
}