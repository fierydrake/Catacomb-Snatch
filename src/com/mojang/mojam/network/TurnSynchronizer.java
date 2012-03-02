package com.mojang.mojam.network;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.mojang.mojam.MojamComponent;
import com.mojang.mojam.network.packet.PingPacket;
import com.mojang.mojam.network.packet.StartGamePacket;
import com.mojang.mojam.network.packet.SyncCheckPacket;
import com.mojang.mojam.network.packet.TurnPacket;

public class TurnSynchronizer {

    private static SortedMap<Integer,String[]> synchedRandomUseStacks = /*Collections.synchronizedSortedMap(*/new TreeMap<Integer, String[]>()/*)*/;
    private static List<String> synchedRandomStacksCurrentTurn = /*Collections.synchronizedList(*/new ArrayList<String>(150000)/*)*/;
    private static boolean shownCacheWarningThisTurn = false;
    private static int synchedRandomCounter = 0; // If this wraps, it should be okay we just need a recent match
	public static Random synchedRandom = new Random() {
	    @Override
	    public int next(int bits) {
	        synchedRandomCounter++;
	        if (synchedRandomStacksCurrentTurn.size() < 150000) {
	            StringBuilder stack = new StringBuilder(Thread.currentThread().getName()+"\n");
	            for (StackTraceElement frame:Thread.currentThread().getStackTrace()) {
	                stack.append(frame.toString()+"\n");
	            }
	            synchedRandomStacksCurrentTurn.add(stack.toString());
	        } else {
	            if (!shownCacheWarningThisTurn) {
	                // only show warning once
	                System.err.println("WARNING: Overflowed syncedRandom debug stack cache");
	                shownCacheWarningThisTurn = true;
	            }
	        }
	        return super.next(bits);
	    }
	};
	public static long synchedSeed;

	public static final int TURN_QUEUE_LENGTH = 3;
	public static final int TICKS_PER_TURN = 5;

	private int currentTurnLength = TICKS_PER_TURN;

	private List<NetworkCommand> nextTurnCommands = new ArrayList<NetworkCommand>();
	private PlayerTurnCommands playerCommands;
	private final int numPlayers;

	private TurnInfo[] turnInfo = new TurnInfo[TURN_QUEUE_LENGTH];
	private int commandSequence = TURN_QUEUE_LENGTH - 1;
	private int turnSequence = 0;
	private int currentTurnTickCount;

	private final PacketLink packetLink;
	private int localId;
	private final CommandListener commandListener;

	private boolean isStarted;

	public TurnSynchronizer(CommandListener commandListener,
			PacketLink packetLink, int localId, int numPlayers) {

		this.commandListener = commandListener;
		this.packetLink = packetLink;
		this.localId = localId;
		this.numPlayers = numPlayers;
		this.playerCommands = new PlayerTurnCommands(numPlayers);

		for (int i = 0; i < turnInfo.length; i++) {
			turnInfo[i] = new TurnInfo(i, numPlayers);
		}
		turnInfo[0].isDone = true;
		turnInfo[1].isDone = true;

		synchedSeed = synchedRandom.nextLong();
		synchedRandom.setSeed(synchedSeed);

	}

	public int getLocalTick() {
		return turnSequence;
	}

	public synchronized boolean preTurn() {

		if (!isStarted) {
			return false;
		}

		int currentTurn = turnSequence % turnInfo.length;
		if (turnInfo[currentTurn].isDone
				|| playerCommands.isAllDone(turnSequence)) {
			turnInfo[currentTurn].isDone = true;

			if (!turnInfo[currentTurn].isCommandsPopped) {
				turnInfo[currentTurn].isCommandsPopped = true;

				for (int i = 0; i < numPlayers; i++) {
					List<NetworkCommand> commands = playerCommands
							.popPlayerCommands(i, turnSequence);
					if (commands != null) {
						for (NetworkCommand command : commands) {
							commandListener.handle(i, command);
						}
					}
				}
			}
			return true;
		} else {
			// System.out.println("Stalled");
		}
		return false;
	}

	public synchronized void postTurn() {

		currentTurnTickCount++;
		if (currentTurnTickCount >= currentTurnLength) {

			int currentTurn = turnSequence % turnInfo.length;
			turnInfo[currentTurn].clearDone();
			turnInfo[currentTurn].turnNumber += TURN_QUEUE_LENGTH;

			turnSequence++;
			currentTurnTickCount = 0;

			playerCommands.addPlayerCommands(localId, commandSequence,
					nextTurnCommands);
			sendLocalTurn(turnInfo[commandSequence % turnInfo.length]);
			commandSequence++;
			nextTurnCommands = null;
			
			synchedRandomUseStacks.put(turnSequence, synchedRandomStacksCurrentTurn.toArray(new String[0]));
			synchedRandomStacksCurrentTurn.clear();
			shownCacheWarningThisTurn = false;
			if (turnSequence%50 == 0) sendSyncCheckPacket(); 
		}
		if (turnSequence%50 == 0) sendPingPacket();
	}

	public synchronized void addCommand(NetworkCommand command) {

		if (nextTurnCommands == null) {
			nextTurnCommands = new ArrayList<NetworkCommand>();
		}
		nextTurnCommands.add(command);

	}

	private void sendLocalTurn(TurnInfo turnInfo) {

		if (packetLink != null) {
			packetLink.sendPacket(turnInfo.getLocalPacket(nextTurnCommands));
		}

	}
	
	private void sendPingPacket() {
	    if (packetLink != null) {
	        packetLink.sendPacket(new PingPacket());
	    }
	}

	private Map<Integer, Integer> syncCheckCache = new HashMap<Integer, Integer>();
	private void sendSyncCheckPacket() {
	    if (packetLink != null) {
	        if (syncCheckCache.size() < 100) {
                System.err.println("INFO   : Sync check cache size="+syncCheckCache.size()+" (about to add 1 entry)");
    	        syncCheckCache.put(turnSequence, synchedRandomCounter);
    	        System.err.println("DEBUG  : Sending synccheck "+turnSequence);
    	        packetLink.sendPacket(new SyncCheckPacket(turnSequence, synchedRandomCounter));
    	        if (packetCache.size()>0) {
    	            for (SyncCheckPacket cached : packetCache) {
    	                if (cached.getTurn() == turnSequence) {
    	                    System.err.println("INFO   : Resolved out-of-sequence sync packet for turn " + turnSequence);
    	                    System.err.println("DEBUG  : Packet cache size="+packetCache.size()+" (about to remove 1 entry)");
    	                    performSyncCheck(turnSequence, synchedRandomCounter, cached.getCount());
    	                    packetCache.remove(cached);
    	                    syncCheckCache.remove(turnSequence);
    	                    cleanupStacksCache(turnSequence);
    	                    break;
    	                }
    	            }
    	        }
	        } else {
	            System.err.println("WARNING: Sync check hash full, skipping sync check packet for turn " + turnSequence);
	        }
	    }
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public synchronized void onTurnPacket(TurnPacket packet) {
		playerCommands.addPlayerCommands(packet.getPlayerId(),
				packet.getTurnNumber(), packet.getPlayerCommandList());
	}

	public synchronized void onStartGamePacket(StartGamePacket packet) {
		setStarted(true);
		synchedSeed = packet.getGameSeed();
		synchedRandom.setSeed(synchedSeed);
	}

	public synchronized void onPingPacket(PingPacket packet) {
	    if (packet.getType() == PingPacket.TYPE_SYN && packetLink != null) {
	        packetLink.sendPacket(PingPacket.ack(packet));
	    }
	}
	
	private List<SyncCheckPacket> packetCache = new ArrayList<SyncCheckPacket>();
	public synchronized void onSyncCheckPacket(SyncCheckPacket packet) {
	    Integer localCount = syncCheckCache.get(packet.getTurn());
	    int remoteCount = packet.getCount();
	    boolean deferCleanup = false;
	    if (localCount != null) {
	        syncCheckCache.remove(packet.getTurn());
	        performSyncCheck(packet.getTurn(), localCount, remoteCount);
	    } else {
	        System.err.println("WARNING: Receive sync check packet for a turn that was not cached (turn " + packet.getTurn() + ")");
	        if (packetCache.size() < 100) {
	            deferCleanup = true;
	            packetCache.add(packet);
	        } else {
	            System.err.println("WARNING: Packet cache overflow");
	        }
	    }
	    
        // remove old stuff from stack cache
	    if (!deferCleanup) {
	        cleanupStacksCache(packet.getTurn());
	    } else {
	        System.err.println("DEBUG  : Defer cleanup of stacks cache");
	    }
	}
	
	private void cleanupStacksCache(int turn) {
        System.err.println("DEBUG  : Before clean: Stacks cache size="+synchedRandomUseStacks.size());
        Iterator<Integer> it = synchedRandomUseStacks.keySet().iterator();
        int key, cleaned=0;
        while (it.hasNext() && (key = it.next()) <= turn) {
            cleaned+=synchedRandomUseStacks.get(key).length;
            it.remove();
        }
        System.err.println("DEBUG  : After clean : Stacks cache size="+synchedRandomUseStacks.size() + " (cleaned "+cleaned+" stacks)");	    
	}
	
	private void performSyncCheck(int turn, int localCount, int remoteCount) {
        if (localCount != remoteCount) {
            System.err.println("WARNING: >>");
            System.err.println("WARNING: >> Remote syncedRandom count ("+remoteCount+") for turn " + turn + " does not match local count ("+localCount+"). Client and server out of sync.");
            System.err.println("WARNING: >>");
            // print stacks to check
            System.err.println("Dumping all call stacks to synchedRandom since last good sync:");
            System.err.println("-----");
            PrintStream out = System.err;
            try {
                out = new PrintStream("syncdump-"+MojamComponent.localTeam+".txt");
            } catch (FileNotFoundException e) {
                System.err.println("ERROR  : Failed to open a new file syncdump.txt in the current working directory, falling back to stderr");
            }
            for (int t: synchedRandomUseStacks.keySet()) {
                out.println("-----");
                out.println("TURN " + t);
                out.println("-----");
                int i=0;
                for (String stack : synchedRandomUseStacks.get(t)) {
                    out.println(">> Stack "+t+"."+i);
                    out.println(stack);
                    i++;
                }
            }
            if (out != System.err) {
                out.close();
            }
            System.exit(1);
        } else {
            System.err.println("INFO   : *");
            System.err.println("INFO   : * Sync check OK (turn="+turn+", count="+localCount + ")");
            System.err.println("INFO   : *");
            Runtime r = Runtime.getRuntime();
            System.err.println("DEBUG  : Heap: " + (r.totalMemory()/(1024*1024)) + " MB /" + (r.maxMemory()/(1024*1024)) + " MB ("+(r.freeMemory()/(1024*1024)) +" MB free)");
        }
	}
	
	private class TurnInfo {

		public boolean isCommandsPopped;
		public boolean isDone;
		private int turnNumber;

		public TurnInfo(int turnNumber, int numPlayers) {
			this.turnNumber = turnNumber;
		}

		public void clearDone() {
			isDone = false;
			isCommandsPopped = false;
		}

		public TurnPacket getLocalPacket(
				List<NetworkCommand> localPlayerCommands) {
			return new TurnPacket(localId, turnNumber, localPlayerCommands);
		}

		// public void onReceivedCommands(int playerId, List<NetworkCommand>
		// list) {
		// playerDone[playerId] = true;
		// playerCommands.set(playerId, list);
		//
		// checkDone();
		// }
		//
		// private void checkDone() {
		// isDone = true;
		// for (int i = 0; i < playerDone.length; i++) {
		// if (!playerDone[i]) {
		// isDone = false;
		// break;
		// }
		// }
		// }
	}

}
