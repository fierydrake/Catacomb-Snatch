package com.mojang.mojam.level;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mojang.mojam.CatacombSnatch;

public class LevelList {

	private static final List<LevelInformation> VANILLA_LEVELS;
	static {
		List<LevelInformation> vlevels = new ArrayList<LevelInformation>();
		vlevels.add(new LevelInformation("Mojam", "/levels/level1.bmp",true));
		vlevels.add(new LevelInformation("AsymeTrical","/levels/AsymeTrical.bmp",true));
		vlevels.add(new LevelInformation("CataBOMB", "/levels/CataBOMB.bmp",true));
		vlevels.add(new LevelInformation("Siege","/levels/Siege.bmp",true));
		vlevels.add(new LevelInformation("TheMaze", "/levels/TheMaze.bmp",true));
		vlevels.add(new LevelInformation("Circular_Shapes", "/levels/Circular Shapes.bmp",true));
		vlevels.add(new LevelInformation("BlackHole", "/levels/BlackHole.bmp",true));
		vlevels.add(new LevelInformation("Railroads", "/levels/RailRoads.bmp",true));
		vlevels.add(new LevelInformation("DevMap", "/levels/DevMap.bmp",true));
		VANILLA_LEVELS = Collections.unmodifiableList(vlevels);
	}
	
	private static List<LevelInformation> levels;
	
	public static void createLevelList() {
		levels = new ArrayList<LevelInformation>(VANILLA_LEVELS);
		
		File levels = getBaseDir();
		if(!levels.exists()) levels.mkdirs();
		System.out.println("Looking for levels: "+levels.getPath());
		loadDir(levels);
	}
	
	public static File getBaseDir(){
		return new File(CatacombSnatch.getExternalsDir(), "levels");
	}
	
	public static void loadDir(File file){
		File[] children = file.listFiles();
	    if (children != null) {
	        for (File child : children) {
	            if(child.isDirectory()){
	            	loadDir(child);
	            	continue;
	            }
	            String fileName = child.getName();
	            String fname="";
	            String ext="";
	            int mid= fileName.lastIndexOf(".");
	            fname=fileName.substring(0,mid);
	            ext=fileName.substring(mid+1);
	            System.out.println("  Found level: "+fname+" . "+ext);
	            if(ext.toLowerCase().equals("bmp")){
	        		levels.add(new LevelInformation("+ "+fname, child.getPath(), false));
	            }
	        }
	    }
	}

	public static List<LevelInformation> getLevels() {
		if (levels == null) {
			createLevelList();
		}
		return levels;
	}
	
	public static void resetLevels(){
		levels = null;
	}
	
	public static LevelInformation getForPath(String s) {
		System.out.println("Path -> info: "+s);
		for (LevelInformation level : getLevels()) {
			if (s.equals(level.getPath()) || LevelInformation.sanitizePath(level.getPath()).equals(s)) {
				return level;
			}
		}
		return null;
	}
}
