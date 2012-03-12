package com.mojang.mojam.gui.menus;

import java.awt.event.KeyEvent;
import java.util.List;

import com.mojang.mojam.gameinput.InputBindings.InputBinding;
import com.mojang.mojam.gameinput.LocalGameInput;
import com.mojang.mojam.gameinput.PhysicalInput;
import com.mojang.mojam.gameinput.PhysicalInputs;
import com.mojang.mojam.gameview.GameView;
import com.mojang.mojam.gui.Font;
import com.mojang.mojam.gui.components.BackButton;
import com.mojang.mojam.gui.components.Button;
import com.mojang.mojam.gui.components.ClickableComponent;
import com.mojang.mojam.resources.Texts;
import com.mojang.mojam.screen.Art;
import com.mojang.mojam.screen.Bitmap;
import com.mojang.mojam.screen.Screen;

public class KeyBindingsMenu extends GuiMenu {

	class InputBindingButton extends Button {
		private int id;
		private InputBinding binding;
		private boolean selected = false;

		public InputBindingButton(int id, InputBinding binding, int x, int y) {
			super(binding.toString(), x, y, false);
			this.id = id;
			this.binding = binding;
		}
		
		@Override
		public String labelText() {
			return binding.toString();
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}
		
		@Override
		public void render(Screen screen) {
			super.render(screen);
			int x = getX();
			int y = getY() + 8;
			String prompt = Texts.current().logicalInput(binding.getLogicalInputName())+ ":";
			Font.defaultFont().draw(screen, prompt, x, y, Font.Align.RIGHT); 
		}
		
		@Override
		protected void blitBackground(Screen screen, int bitmapId) {
			super.blitBackground(screen, selected ? 1 : bitmapId);
		}

		public void refresh() {
			// FIXME? Need this next line because when a new input is mapped 
			//        the InputBinding object in this button is not updated
			//        (see comment in InputBindings)
			this.binding = menus.getLocalInput().getBindings().get(binding.getLogicalInputName());
			updateLabel();
		}
	}

	private static final int BORDER = 10;
	private static final int BUTTON_SPACING = 28;

	private int textWidth;
	private int yOffset;
	private int numRows;

	private ClickableComponent back;
	private InputBindingButton selectedButton = null;

	public KeyBindingsMenu() {
		super();

		List<InputBinding> bindings = menus.getLocalInput().getBindings().getAll();

		textWidth = (GameView.WIDTH - 3 * BORDER - 2 * Button.WIDTH) / 2;
		numRows = bindings.size() / 2;
		int tab1 = BORDER + textWidth;
		int tab2 = GameView.WIDTH - BORDER - Button.WIDTH;
		yOffset = (GameView.HEIGHT - (numRows * BUTTON_SPACING)) / 2;

		int id = 0, tab = tab1;
		for (InputBinding binding : bindings) {
			addButton(new InputBindingButton(id, binding, tab, yOffset + (id % numRows) * BUTTON_SPACING));
			if (++id > numRows - 1) { tab = tab2; }
		}
		back = addButton(new BackButton((GameView.WIDTH - Button.WIDTH) / 2, 
				yOffset + numRows * BUTTON_SPACING + 16));
	}

	@Override
	public void render(Screen screen) {
		screen.blit(Art.background, 0, 0);
		Texts txts = Texts.current();
		Font.defaultFont().draw(screen, txts.getStatic("options.keyBindings"), screen.w / 2, yOffset - BUTTON_SPACING, Font.Align.CENTERED);

		super.render(screen);
		ClickableComponent button = buttons.get(focusedItem);
		Bitmap playerArt = Art.getLocalPlayerArt()[0][6];
		if (button == back) {
			screen.blit(playerArt, back.getX() - 40, back.getY() - 8);
		} else {
			screen.blit(playerArt, button.getX() - textWidth + (BORDER - playerArt.w) / 2,
					button.getY() - 8);
		}
	}

	@Override
	public void tick(LocalGameInput input) {
		PhysicalInputs inputs = input.getCurrentPhysicalState();
		if (selectedButton != null) {
			/* Capture the next physical input and set as the binding */
			PhysicalInput physicalInput = inputs.consumePress();
			if (physicalInput != null) {
				String logicalInputName = selectedButton.binding.getLogicalInputName();
				input.getBindings().map(logicalInputName, physicalInput);
				selectedButton.setSelected(false);
				selectedButton = null;
				refreshButtons();
			}
		} else {
			if (buttons.get(focusedItem) instanceof InputBindingButton) {
				InputBindingButton bindingButton = (InputBindingButton)buttons.get(focusedItem);
			
				/* Improve default focus navigation */
				if (inputs.wasKeyPressedConsume(KeyEvent.VK_BACK_SPACE)) {
					/* Clear the bindings of the focused button */
					input.getBindings().unmap(bindingButton.binding.getLogicalInputName());
					refreshButtons();
				}
				if (bindingButton.id < numRows) {
					if (inputs.wasKeyPressedConsume(KeyEvent.VK_RIGHT, KeyEvent.VK_D)) {
						focusedItem = bindingButton.id + numRows;
					}
				}
				if (bindingButton.id >= numRows) {
					if (inputs.wasKeyPressedConsume(KeyEvent.VK_LEFT, KeyEvent.VK_A)) {
						focusedItem = bindingButton.id - numRows;
					}
				}
			}
		}
		super.tick(input);
	}
	
	@Override
	public void buttonPressed(ClickableComponent button) {
		if (selectedButton == null && button instanceof InputBindingButton) {
			selectedButton = (InputBindingButton)button;
			selectedButton.setSelected(true);
		}
	}
	
	public void refreshButtons() {
		for (ClickableComponent button : buttons) {
			if (button instanceof InputBindingButton) {
				((InputBindingButton)button).refresh();
			}
		}
	}
}
