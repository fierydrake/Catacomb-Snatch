package com.mojang.mojam.gameinput;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.mojam.Options;
import com.mojang.mojam.gameinput.LogicalInputs.LogicalInput;
import com.mojang.mojam.gameinput.PhysicalInput.Key;
import com.mojang.mojam.gameinput.PhysicalInput.MouseButton;
import com.mojang.mojam.gameview.GameView;

/*
 * This class is responsible for converting local AWT input sources
 * into game input. 
 * 
 * It maintains mappings between physical inputs (like keys and mouse 
 * buttons) and logical inputs (like fire and movement); and it
 * receives input events from AWT and bundles them up into discrete
 * per-tick batches of input state.
 * 
 * Each logical input can be mapped against up to 3 physical inputs.
 * This is an arbitrary limit.
 */
public class LocalGameInput extends BaseGameInput implements KeyListener, MouseListener, MouseMotionListener {
	public static final int MAX_PHYSICAL_PER_LOGICAL = 3;
	private Map<PhysicalInput, String> physicalToLogicalInputMapping = new HashMap<PhysicalInput, String>();
	private Map<String, List<PhysicalInput>> logicalToPhysicalInputMappings = new HashMap<String, List<PhysicalInput>>();
	
	private PhysicalInputs currentPhysical = new PhysicalInputs();
	private PhysicalInputs nextPhysical = new PhysicalInputs();

	public LocalGameInput() {
		super();
		/* 
		 * Add mappings for all logical inputs.
		 * These will be loaded from Options, if available,
		 * and fall back to the defaults specified.
		 */
		// controls
		initKey(next.up,     KeyEvent.VK_W);
		initKey(next.down,   KeyEvent.VK_S);
		initKey(next.left,   KeyEvent.VK_A);
		initKey(next.right,  KeyEvent.VK_D);
		initKey(next.sprint, KeyEvent.VK_SHIFT);

		// actions
		initInput(next.fire, Key.get(KeyEvent.VK_SPACE), MouseButton.get(MouseEvent.BUTTON1));
		initInput(next.use,  Key.get(KeyEvent.VK_E),     MouseButton.get(MouseEvent.BUTTON3));
		initKey(next.fireUp,     KeyEvent.VK_UP);
		initKey(next.fireDown,   KeyEvent.VK_DOWN);
		initKey(next.fireLeft,   KeyEvent.VK_LEFT);
		initKey(next.fireRight,  KeyEvent.VK_RIGHT);
		initKey(next.build,      KeyEvent.VK_R);
		initKey(next.upgrade,    KeyEvent.VK_F);
		initKey(next.pause,      KeyEvent.VK_ESCAPE);
		initKey(next.screenShot, KeyEvent.VK_F2);
		initKey(next.fullscreen, KeyEvent.VK_F11);
		initKey(next.chat,       KeyEvent.VK_T);
		
		//console
		initKey(next.console, KeyEvent.VK_TAB);
	}
	
	public LocalGameInput(Component localInputComponent) {
		this();
		localInputComponent.addKeyListener(this);
		localInputComponent.addMouseListener(this);
		localInputComponent.addMouseMotionListener(this);
	}

	private void initKey(LogicalInput logicalInput, int defaultKeyCode) {
		initInput(logicalInput, Key.get(defaultKeyCode));
	}
	
	@SuppressWarnings("unused")
	private void initMouseButton(LogicalInput logicalInput, int defaultMouseButtonCode) {
		initInput(logicalInput, MouseButton.get(defaultMouseButtonCode));
	}
	
	private void initInput(LogicalInput logicalInput, PhysicalInput... defaultPhysicalInputs) {
		PhysicalInput[] physicalInputs = Options.getAsArrayOfPhysicalInputs("binding_" + logicalInput.name, defaultPhysicalInputs);
		
		for (PhysicalInput physicalInput : physicalInputs) {
			mapInput(logicalInput, physicalInput);
		}
	}

	private void mapInput(LogicalInput logical, PhysicalInput physical) {
		/* Remove any existing mappings to this physical input */
		clearMappingOf(physical);
		
		/* Ensure this logical input is keyed in the map */
		if (!logicalToPhysicalInputMappings.containsKey(logical.name)) {
			logicalToPhysicalInputMappings.put(logical.name, new ArrayList<PhysicalInput>());
		}
		
		/* Cap the number of bound physical inputs for this logical input */
		if (logicalToPhysicalInputMappings.get(logical.name).size() == MAX_PHYSICAL_PER_LOGICAL) {
			logicalToPhysicalInputMappings.remove(0);
		}

		/* Add the new mapping */
		logicalToPhysicalInputMappings.get(logical.name).add(physical);
		physicalToLogicalInputMapping.put(physical, logical.name);
	}
	
	private void clearMappingOf(PhysicalInput physical) {
		/* If this physical key is mapped to a logical key... */
		if (physicalToLogicalInputMapping.containsKey(physical)) {
			/* ...remove the physical key from that mapping*/
			List<PhysicalInput> physicalInputs = logicalToPhysicalInputMappings.get(physical);
			physicalInputs.remove(physical);
			physicalToLogicalInputMapping.remove(physical);
		}
	}
	
	@Override
	public synchronized void gatherInput() {
		super.gatherInput();
		/* Advance current state */
		nextPhysical.copyInto(currentPhysical);
		/* Reset any per tick flags */
		nextPhysical.reset();
	}

	public PhysicalInputs getCurrentPhysicalState() {
		return currentPhysical;
	}
	
	public Point getMousePosition() {
		return currentPhysical.getMousePosition();
	}
	
	/*
	 * Implement KeyListener
	 * Accrue results of events in the next State#keys
	 */
	
	@Override
	public synchronized void keyPressed(KeyEvent e) {
		PhysicalInput keyPressed = Key.get(e.getKeyCode());
		if (physicalToLogicalInputMapping.containsKey(keyPressed)) {
			LogicalInput logicalInput = next.getLogicalInputByName(physicalToLogicalInputMapping.get(keyPressed));
			logicalInput.wasPressed = true;
			logicalInput.isDown = true;
		}
		nextPhysical.addPress(keyPressed);
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		PhysicalInput keyReleased = Key.get(e.getKeyCode());
		if (physicalToLogicalInputMapping.containsKey(keyReleased)) {
			LogicalInput logicalInput = next.getLogicalInputByName(physicalToLogicalInputMapping.get(keyReleased));
			logicalInput.wasReleased = true;
			logicalInput.isDown = false;
		}
		nextPhysical.addRelease(keyReleased);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// Nothing, we should be covered by keyPressed and keyReleased
	}

	/* 
	 * Implement MouseListener
	 * Accrue results of events in the next State#mouse
	 */
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// Nothing, we should be covered by mousePressed and mouseReleased
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
	}
	
	@Override
	public synchronized void mousePressed(MouseEvent e) {
		PhysicalInput mouseButtonPressed = MouseButton.get(e.getButton());
		if (physicalToLogicalInputMapping.containsKey(mouseButtonPressed)) {
			LogicalInput logicalInput = next.getLogicalInputByName(physicalToLogicalInputMapping.get(mouseButtonPressed));
			logicalInput.wasPressed = true;
			logicalInput.isDown = true;
		}
		nextPhysical.addPress(mouseButtonPressed);
	}
	
	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		PhysicalInput mouseButtonReleased = MouseButton.get(e.getButton());
		if (physicalToLogicalInputMapping.containsKey(mouseButtonReleased)) {
			LogicalInput logicalInput = next.getLogicalInputByName(physicalToLogicalInputMapping.get(mouseButtonReleased));
			logicalInput.wasReleased = true;
			logicalInput.isDown = false;
		}
		nextPhysical.addRelease(mouseButtonReleased);
	}
	
	/* 
	 * Implement MouseMotionListener
	 */
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}
	
	@Override
	public synchronized void mouseMoved(MouseEvent e) {
		nextPhysical.setMouseMoved();
		nextPhysical.setMousePosition(e.getX() / GameView.SCALE, e.getY() / GameView.SCALE);
	}
}


