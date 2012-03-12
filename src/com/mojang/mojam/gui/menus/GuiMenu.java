package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import com.mojang.mojam.CatacombSnatch;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gui.ButtonListener;
import com.mojang.mojam.gui.GuiElement;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.gui.components.LabelledClickableComponent;
import com.mojang.mojam.gui.components.Text;
import com.mojang.mojam.math.Mth;
import com.mojang.mojam.resources.LocaleChangeListener;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Screen;

public abstract class GuiMenu extends GuiElement implements ButtonListener, LocaleChangeListener {
	protected List<ClickableComponent> buttons = new ArrayList<ClickableComponent>();
	protected List<Text> texts = new ArrayList<Text>();
	protected int focusedItem = 0;

	public GuiMenu() {
		Texts.addLocaleListener(this);
	}

	protected ClickableComponent addButton(ClickableComponent button) {
		buttons.add(button);
		button.addListener(this);
		return button;
	}
	
    protected ClickableComponent removeButton(ClickableComponent button) {
        if (buttons.remove(button)) {
        	button.removeListener(this);
        	focusedItem = Mth.clamp(focusedItem, 0, buttons.size()-1);
            return button;
        } else {
            return null;
		}
	}

	protected Text addText(Text text) {
		texts.add(text);
		return text;
	}
    
    protected Text removeText(Text text) {
        if (texts.remove(text)) {
            return text;
        } else {
            return null;
        }
    }
    
    @Override
    public void tick(LocalGameInput input) {
    	super.tick(input);
    	
    	if (input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_UP, KeyEvent.VK_W)) {
			focusedItem--;
			if (focusedItem < 0) {
				focusedItem = buttons.size() - 1;
			}    		
    	}
    	if (input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_DOWN, KeyEvent.VK_S)) {
			focusedItem++;
			if (focusedItem > buttons.size() - 1) {
				focusedItem = 0;
			}
    	}
    	if (input.getCurrentPhysicalState().wasKeyPressed(KeyEvent.VK_F11)) {
			CatacombSnatch.toggleFullscreen();
		}
    	
    	for (ClickableComponent button : buttons) {
    		button.focused = (button == buttons.get(focusedItem));
    		button.tick(input);
    	}
    }
    
	@Override
	public void render(Screen screen) {
		super.render(screen);

		for (ClickableComponent button : buttons) {
			button.render(screen);
		}
		for (Text text : texts) {
			text.render(screen);
		}
	}

	/* 
	 * Implement ButtonListener
	 */
	
	@Override
	public void buttonHovered(ClickableComponent clickableComponent) {
		focusedItem = buttons.indexOf(clickableComponent);
	}
	
	@Override
	public void buttonPressed(ClickableComponent clickableComponent) {
	}
		
	/*
	 * LocaleChangeListener
	 */
	@Override
	public void localeChanged() {
		for (ClickableComponent button : buttons) {
			if (button instanceof LabelledClickableComponent) {
				((LabelledClickableComponent) button).updateLabel();
			}
		}
	}
}
