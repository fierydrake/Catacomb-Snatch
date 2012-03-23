package com.mojang.mojam.network.debug;

import static com.mojang.mojam.CatacombSnatch.logic;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.mojang.mojam.gamelogic.SyncClientGameLogic;
import com.mojang.mojam.network.TurnSynchronizer;

public class DebugSynchedRandom extends Random {
    private static final long serialVersionUID = 1L;
    private static Map<Long, String> stackFlywheel = new HashMap<Long, String>();
    private static SortedMap<Integer,String[]> useStacks = new TreeMap<Integer, String[]>();
    private static List<String> useStacksCurrentTurn = new ArrayList<String>(150000);
    private static boolean shownCacheWarningThisTurn = false;
    private int count = 0; // If this wraps, it should be okay we just need a recent match

    public DebugSynchedRandom() {
    }
    
    public int getCount() { return count; }
    
    @Override
    public int next(int bits) {
        count++;
        if (useStacksCurrentTurn.size() < 150000) {
            StringBuilder currentStack = new StringBuilder();
            currentStack.append(Thread.currentThread().getName()+"\n");
            for (StackTraceElement frame:Thread.currentThread().getStackTrace()) {
                currentStack.append(frame.toString()+"\n");
            }
            String stack = currentStack.toString();
            long flywheelHash = stack.hashCode();

            if (!stackFlywheel.containsKey(flywheelHash)) {
                stackFlywheel.put(flywheelHash, stack.intern());
            }
            useStacksCurrentTurn.add(stackFlywheel.get(flywheelHash));
        } else {
            if (!shownCacheWarningThisTurn) {
                // only show warning once
            	if (!TurnSynchronizer.SYNC_CHECK_QUIET) System.err.println("WARNING: Overflowed syncedRandom debug stack cache");
                shownCacheWarningThisTurn = true;
            }
        }
        return super.next(bits);
    }
    
    public void postTurn(int turn) {
        useStacks.put(turn, useStacksCurrentTurn.toArray(new String[0]));
        useStacksCurrentTurn.clear();
        shownCacheWarningThisTurn = false;
    }
    
    public void cleanupStacksCache(int turn) {
    	if (!TurnSynchronizer.SYNC_CHECK_QUIET) System.err.println("DEBUG : Before clean: Stacks cache size=" + useStacks.size());
        Iterator<Integer> it = useStacks.keySet().iterator();
        int key, cleaned=0;
        while (it.hasNext() && (key = it.next()) <= turn) {
            cleaned += useStacks.get(key).length;
            it.remove();
        }
        if (!TurnSynchronizer.SYNC_CHECK_QUIET) System.err.println("DEBUG : After clean : Stacks cache size=" + useStacks.size() + " (cleaned "+cleaned+" stacks)");
    }
    
    public void performSyncCheck(int turn, int localCount, int remoteCount) {
        if (localCount != remoteCount) {
            System.err.println("WARNING: >>");
            System.err.println("WARNING: >> Remote syncedRandom count ("+remoteCount+") for turn " + turn + " does not match local count ("+localCount+"). Client and server out of sync.");
            System.err.println("WARNING: >>");
            // print stacks to check
            System.err.println("Dumping all call stacks to synchedRandom since last good sync:");
            System.err.println("-----");
            PrintStream out = System.err;
            try {
                out = new PrintStream("syncdump-"+(logic() instanceof SyncClientGameLogic ? "client" : "server")+".txt");
            } catch (FileNotFoundException e) {
                System.err.println("ERROR : Failed to open a new file syncdump.txt in the current working directory, falling back to stderr");
            }
            for (int t: useStacks.keySet()) {
                out.println("-----");
                out.println("TURN " + t);
                out.println("-----");
                int i=0;
                for (String stack : useStacks.get(t)) {
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
        	if (!TurnSynchronizer.SYNC_CHECK_QUIET) {
        		System.err.println("INFO : *");
        		System.err.println("INFO : * Sync check OK (turn="+turn+", count="+localCount + ") [" + stackFlywheel.size() + "]");
        		System.err.println("INFO : *");
        		Runtime r = Runtime.getRuntime();
        		System.err.println("DEBUG : Heap: " + (r.totalMemory()/(1024*1024)) + " MB /" + (r.maxMemory()/(1024*1024)) + " MB ("+(r.freeMemory()/(1024*1024)) +" MB free)");
        	}
        }
    }
}