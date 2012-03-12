package com.mojang.mojam.gameinput;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import com.mojang.mojam.resources.Texts;


public abstract class PhysicalInput {
	/*
	 * An abstract factory for making types of physical input
	 */
	
	protected interface PhysicalInputFactory {
		public PhysicalInput create(int code);
	}
	
	protected static Map<String, PhysicalInputFactory> factories = new HashMap<String, PhysicalInputFactory>();

	private static PhysicalInput create(String source, int code) {
		PhysicalInputFactory factory = factories.get(source);
		return factory.create(code);
	}
	
	/* 
	 * Concrete types of physical input
	 */
	
	static class Key extends PhysicalInput {
		static {
			PhysicalInput.factories.put("key", new PhysicalInputFactory() { 
				@Override 
				public PhysicalInput create(int code) { return new Key(code); } 
			});
		}
		
		public static PhysicalInput get(int keyCode) {
			return PhysicalInput.get("key", keyCode);
		}
		
		private Key(int keyCode) {
			super("key", keyCode);
		}
		
		public String getDisplayName() {
			return KeyEvent.getKeyText(code);
		}
	}
	
	static class MouseButton extends PhysicalInput {
		static {
			PhysicalInput.factories.put("mouse", new PhysicalInputFactory() { 
				@Override 
				public PhysicalInput create(int code) { return new MouseButton(code); } 
			});
		}
		
		public static PhysicalInput get(int buttonCode) {
			return PhysicalInput.get("mouse", buttonCode);
		}
		
		private MouseButton(int buttonCode) {
			super("mouse", buttonCode);
		}
		
		public String getDisplayName() {
			switch (code) {
			case MouseEvent.BUTTON1: return Texts.current().getStatic("mouse.button1");
			case MouseEvent.BUTTON2: return Texts.current().getStatic("mouse.button2");
			case MouseEvent.BUTTON3: return Texts.current().getStatic("mouse.button3");
			}
			throw new RuntimeException("Invalid mouse button code");
		}
	}

	/*
	 * Store all physical inputs in a flywheel
	 */
	private static Map<String, PhysicalInput> flywheel = new HashMap<String, PhysicalInput>();
	
	public static PhysicalInput get(String source, int code) {
		String flywheelKey = flywheelKeyFor(source, code);
		if (!flywheel.containsKey(flywheelKey)) {
			flywheel.put(flywheelKey, create(source, code));
		}
		return flywheel.get(flywheelKey);	
	}
	
	private static String flywheelKeyFor(String source, int code) {
		return source+code;
	}
	
	/*
	 * Main definition of physical input 
	 */
	
	String source;
	int code;
	
	protected PhysicalInput(String source, int code) {
		if (source == null) throw new IllegalArgumentException("Physical input source must not be null");
		this.source = source;
		this.code = code;
	}
	
	@Override 
	public boolean equals(Object o) {
		if (!(o instanceof PhysicalInput)) return false;
		return ((PhysicalInput)o).source.equals(source) && ((PhysicalInput)o).code == code;
	}
	@Override 
	public int hashCode() { return (source+code).hashCode(); }
	
	public abstract String getDisplayName();
}