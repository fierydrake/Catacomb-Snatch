package com.mojang.mojam.gui.components;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gui.ButtonListener;

public abstract class ClickableComponent extends VisibleComponent {
	public static boolean DEBUG_FOCUS = false;

	private List<ButtonListener> listeners;

	private boolean isPressed;
	public boolean enabled = true;
	public boolean focused = false;

	public ClickableComponent(int x, int y, int w, int h) {
		super(x, y, w, h);
	}

	@Override
	public void tick(LocalGameInput input) {
		if (contains(input.getCurrentPhysicalState().getMousePosition())) {
			if (enabled) {
				if (isPressed || input.isMouseActive()) {
					for (ButtonListener listener : listeners) {
						listener.buttonHovered(this);
					}
				}
				if (isPressed && input.getCurrentPhysicalState().wasMouseButtonReleased(MouseEvent.BUTTON1)) {
					postClick();
					isPressed = false;
					
				} else if (input.getCurrentPhysicalState().wasMouseButtonPressed(MouseEvent.BUTTON1)) {
					isPressed = true;
				}
			}
		} else if (isPressed && input.getCurrentPhysicalState().wasMouseButtonReleased(MouseEvent.BUTTON1)) {
			isPressed = false;
		}
		if (focused && input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_ENTER)) {
			if (!(this instanceof Slider)) { /* Prevent double dispatch of click */
				postClick();
			}
		}
	}
	
	public void postClick() {
		clicked();
		for (ButtonListener listener : listeners) {
			listener.buttonPressed(this);
		}
	}

	/**
	 * This component is being clicked on?
	 * 
	 * @return boolean
	 */
	public boolean isPressed() {
		return isPressed;
	}

	/**
	 * Adds a listener to the internal list, to get called when this component
	 * has been clicked
	 * 
	 * @param listener
	 */
	public void addListener(ButtonListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<ButtonListener>();
		}
		listeners.add(listener);
	}

	/**
	 * Removes a listener from the internal list
	 * 
	 * @param listener
	 */
	public void removeListener(ButtonListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Triggered when clicked
	 * 
	 * @param e Mouse event that generated the click
	 */
	protected void clicked() {}

}
