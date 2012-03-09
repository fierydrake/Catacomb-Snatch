package com.mojang.mojam.level;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.mojang.mojam.CatacombSnatch;

public class LevelInformation {
	public static HashMap<String, LevelInformation> fileToInfo = new HashMap<String, LevelInformation>();
	private static int localIDcounter = 0;
	
	public int localID;
	public String levelName;
	private String levelFile;
	public String levelAuthor;
	public String levelDescription;
	public boolean vanilla;
	
	public LevelInformation(String levelName, String levelFile, boolean vanilla) {
		this.levelName = levelName;
		this.levelFile = vanilla ? levelFile : sanitizePath(levelFile);
		this.vanilla = vanilla;
		
		localID = localIDcounter++;
		fileToInfo.put(levelFile, this);
		System.out.println("Map info added: "+levelFile+"("+(vanilla?"vanilla":"external")+")");
	}
	
	public URL getURL() throws MalformedURLException {
		return vanilla ? CatacombSnatch.class.getResource(levelFile) 
					   : new File(CatacombSnatch.getExternalsDir(), levelFile).toURI().toURL();
	}
	
	public static String sanitizePath(String s){
		return s.substring(s.indexOf("levels"));
	}

	public LevelInformation setAuthor(String s){
		this.levelAuthor = s;
		return this;
	}
	
	public LevelInformation setDescription(String s){
		this.levelDescription = s;
		return this;
	}
}
