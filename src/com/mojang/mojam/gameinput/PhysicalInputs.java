package com.mojang.mojam.gameinput;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhysicalInputs {
	public static class PhysicalInputEvent {
		enum Type { PRESS, RELEASE };
		Type type;
		PhysicalInput physicalInput;
		public PhysicalInputEvent(Type type, PhysicalInput physicalInput) {
			this.type = type;
			this.physicalInput = physicalInput;
		}
	}
	private List<PhysicalInputEvent> events = new ArrayList<PhysicalInputEvent>();
	private boolean mouseMoved = false;
	private Point mousePosition = new Point(0, 0);
	
	public void addPress(PhysicalInput pressed) {
		events.add(new PhysicalInputEvent(PhysicalInputEvent.Type.PRESS, pressed));
	}

	public void addRelease(PhysicalInput released) {
		events.add(new PhysicalInputEvent(PhysicalInputEvent.Type.RELEASE, released));
	}
	
	public void setMouseMoved() { mouseMoved = true; }
	public void setMousePosition(int x, int y) { mousePosition = new Point(x, y); }
	
	public boolean wasMouseMoved() { return mouseMoved; }
	public Point getMousePosition() { return mousePosition; }
	
	public boolean wasKeyPressed(int... keyCodes) {
		for (int keyCode : keyCodes) {
			if (wasKeyPressed(keyCode)) return true;
		}
		return false;
	}
	
	public void consumeKeyPresses(int... keyCodes) {
		for (int keyCode : keyCodes) { consumeKeyPresses(keyCode); }
	}
	
	public boolean wasKeyPressedConsume(int... keyCodes) {
		boolean result = false;
		for (int keyCode : keyCodes) { 
			if (wasKeyPressedConsume(keyCode)) result = true;
		}
		return result;
	}

	public boolean wasKeyPressed(int keyCode) { return hasEvent(PhysicalInput.Key.class, PhysicalInputEvent.Type.PRESS, keyCode); }
	public boolean wasKeyReleased(int keyCode) { return hasEvent(PhysicalInput.Key.class, PhysicalInputEvent.Type.RELEASE, keyCode); }
	public boolean wasMouseButtonPressed(int buttonCode) { return hasEvent(PhysicalInput.MouseButton.class, PhysicalInputEvent.Type.PRESS, buttonCode); }
	public boolean wasMouseButtonReleased(int buttonCode) { return hasEvent(PhysicalInput.MouseButton.class, PhysicalInputEvent.Type.RELEASE, buttonCode); }
	public void consumeKeyPresses(int keyCode) { consumeEvents(PhysicalInput.Key.class, PhysicalInputEvent.Type.PRESS, keyCode); }
	public void consumeKeyReleases(int keyCode) { consumeEvents(PhysicalInput.Key.class, PhysicalInputEvent.Type.RELEASE, keyCode); }
	public void consumeMouseButtonPresses(int buttonCode) { consumeEvents(PhysicalInput.MouseButton.class, PhysicalInputEvent.Type.PRESS, buttonCode); }
	public void consumeMouseButtonReleases(int buttonCode) { consumeEvents(PhysicalInput.MouseButton.class, PhysicalInputEvent.Type.RELEASE, buttonCode); }
	public boolean wasKeyPressedConsume(int keyCode) { return consumeEvents(PhysicalInput.Key.class, PhysicalInputEvent.Type.PRESS, keyCode); }
	public boolean wasKeyReleasedConsume(int keyCode) { return consumeEvents(PhysicalInput.Key.class, PhysicalInputEvent.Type.RELEASE, keyCode); }
	public boolean wasMouseButtonPressedConsume(int buttonCode) { return consumeEvents(PhysicalInput.MouseButton.class, PhysicalInputEvent.Type.PRESS, buttonCode); }
	public boolean wasMouseButtonReleasedConsume(int buttonCode) { return consumeEvents(PhysicalInput.MouseButton.class, PhysicalInputEvent.Type.RELEASE, buttonCode); }
	
	private boolean consumeEvents(Class<? extends PhysicalInput> inputClass, PhysicalInputEvent.Type type, int code) {
		boolean consumed = false;
		Iterator<PhysicalInputEvent> i = events.iterator();
		while (i.hasNext()) {
			PhysicalInputEvent event = i.next();
			if (event.type == type && inputClass.isInstance(event.physicalInput) && event.physicalInput.code == code) {
				i.remove();
				consumed = true;
			}
		}
		return consumed;
	}
	
	private boolean hasEvent(Class<? extends PhysicalInput> inputClass, PhysicalInputEvent.Type type, int code) {
		for (PhysicalInputEvent event : events) {
			if (event.type == type && inputClass.isInstance(event.physicalInput) && event.physicalInput.code == code) {
				return true;
			}
		}
		return false;
	}
	
	void copyInto(PhysicalInputs other) {
		other.events.clear();
		other.events.addAll(events);
		other.mouseMoved = mouseMoved;
		other.mousePosition = mousePosition;
	}
	
	void reset() {
		events.clear();
		mouseMoved = false;
	}
}
