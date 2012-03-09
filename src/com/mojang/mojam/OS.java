package com.mojang.mojam;

public enum OS {
    linux,
    solaris,
    windows,
    macos,
    unknown;
    
    public static OS get() {
    	String s = System.getProperty("os.name").toLowerCase();
    	if (s.contains("win")) {
    		return OS.windows;
    	}
    	if (s.contains("mac")) {
    		return OS.macos;
    	}
    	if (s.contains("solaris")) {
    		return OS.solaris;
    	}
    	if (s.contains("sunos")) {
    		return OS.solaris;
    	}
    	if (s.contains("linux")) {
    		return OS.linux;
    	}
    	if (s.contains("unix")) {
    		return OS.linux;
    	} else {
    		return OS.unknown;
    	}
    }
}
