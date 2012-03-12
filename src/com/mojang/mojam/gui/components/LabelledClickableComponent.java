package com.mojang.mojam.gui.components;

import com.mojang.mojam.resources.Texts;

public abstract class LabelledClickableComponent extends ClickableComponent {
	private String staticTextsID;
	private boolean isLocaleAware;
	protected String label;
	
	/*
	 * Creates a LabelledClickableComponent with a locale aware label specified
	 * by the Texts ID. The text of the label will change to the current locale
	 * translation when updateLabel() is called.
	 */
	public LabelledClickableComponent(int x, int y, int w, int h, String staticTextsID) {
		this(x, y, w, h, staticTextsID, true);
	}
	
	/* 
	 * Creates a LabelledClickableComponent with either a locale aware Texts ID or
	 * a normal string (that doesn't change, unless labelText() is overridden).
	 */
	public LabelledClickableComponent(int x, int y, int w, int h, String staticTextsIDorLabel, boolean isLocaleAware) {
		super(x, y, w, h);
		this.isLocaleAware = isLocaleAware;
		
		if (isLocaleAware) {
			staticTextsID = staticTextsIDorLabel;
			updateLabel();
		} else {
			label = staticTextsIDorLabel;
		}
	}
	
	public final void updateLabel() {
		label = labelText();
	}
	
	/*
	 * Returns the current value of the label for this LabelledClickableComponent.
	 * For a locale aware string, it will return the current translation, and for
	 * a normal string, it will return the current value.
	 * 
	 * This method is always called when updateLabel() is called.
	 * 
	 * This method can be overridden to make the label take some other dynamic
	 * value.
	 */
	protected String labelText() {
		return isLocaleAware ? Texts.current().getStatic(staticTextsID) : label;
	}
}
