package com.mojang.mojam.gameinput;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.mojang.mojam.Options;
import com.mojang.mojam.gameinput.LogicalInputs.LogicalInput;
import com.mojang.mojam.gameinput.PhysicalInput.Key;
import com.mojang.mojam.gameinput.PhysicalInput.MouseButton;

/*
 * This class is responsible for converting local AWT input sources
 * into game input. 
 * 
 * It receives input events from AWT and bundles them up into discrete
 * per-tick batches of input state. These bundles come in two types,
 * LogicalInputs for game inputs (see BaseGameInput) and PhysicalInputs
 * for every "real" input event.
 * 
 * LogicalInputs are intended for in-game actions like movement, firing,
 * using etc. They are abstracted from the underlying input mechanism
 * (including bindings) via the GameInput interface and should be safe 
 * to share between clients.
 * 
 * PhysicalInputs are intended for game menus, for navigation and 
 * arbitrary input (like entering text). They are abstracted from the
 * specifics of the input device, although they retain knowledge of the
 * device type.
 * 
 * LogicalInputs and PhysicalInputs are bound together using an instance
 * of InputBindings, which is for use both internally by LocalGameInput
 * in collecting and properly routing input event; and also for use by
 * menus. Bindings are NOT available through the GameInput interface.
 */
public class LocalGameInput extends BaseGameInput implements KeyListener, MouseListener, MouseMotionListener {
	private InputBindings bindings;
	
	private PhysicalInputs currentPhysical = new PhysicalInputs();
	private PhysicalInputs nextPhysical = new PhysicalInputs();

	/*
	 * This is used for scaling the mouse position to match the
	 * pixels of the underlying Screen that the AWT Component renders
	 * (since the component also renders using the same scale)
	 */
	private int scale;
	
	private static int MOUSE_ACTIVE_DELAY = 60;
	private boolean mouseActive = false;
	private int mouseActiveTime;
	
	protected LocalGameInput() {
		super();
		bindings = new InputBindings();
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
	
	public LocalGameInput(Component localInputComponent, int scale) {
		this();
		this.scale = scale;
		localInputComponent.addKeyListener(this);
		localInputComponent.addMouseListener(this);
		localInputComponent.addMouseMotionListener(this);
	}
	
	public InputBindings getBindings() {
		return bindings;
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
			bindings.map(logicalInput, physicalInput);
		}
	}
	
	@Override
	public synchronized void gatherInput() {
		super.gatherInput();
		/* Advance current state */
		nextPhysical.copyInto(currentPhysical);
		/* Reset any per tick flags */
		nextPhysical.reset();
		/* Keep track of mouse activity */
		if (currentPhysical.wasMouseMoved()) {
			mouseActive = true;
			mouseActiveTime = MOUSE_ACTIVE_DELAY;
		} else if (--mouseActiveTime <= 0) {
			mouseActive = false;
		} 
	}
	
    @Override
	public boolean isMouseActive() { return mouseActive; }

	public PhysicalInputs getCurrentPhysicalState() {
		return currentPhysical;
	}
	
    @Override
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
		if (bindings.maps(keyPressed)) {
			LogicalInput logicalInput = next.getLogicalInputByName(bindings.get(keyPressed).getLogicalInputName());
			if (!logicalInput.isDown) { /* ignore multiple presses without releases, they are really auto-repeats */
				logicalInput.wasPressed = true;
				logicalInput.isDown = true;
			}
		}
		nextPhysical.addPress(keyPressed, new Character(e.getKeyChar()));
	}

	@Override
	public synchronized void keyReleased(KeyEvent e) {
		PhysicalInput keyReleased = Key.get(e.getKeyCode());
		if (bindings.maps(keyReleased)) {
			LogicalInput logicalInput = next.getLogicalInputByName(bindings.get(keyReleased).getLogicalInputName());
			logicalInput.wasReleased = true;
			logicalInput.isDown = false;
		}
		nextPhysical.addRelease(keyReleased, new Character(e.getKeyChar()));
	}

	@Override
	public synchronized void keyTyped(KeyEvent e) {
		nextPhysical.addTyped(Key.get(e.getKeyCode()), new Character(e.getKeyChar()));
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
		if (bindings.maps(mouseButtonPressed)) {
			LogicalInput logicalInput = next.getLogicalInputByName(bindings.get(mouseButtonPressed).getLogicalInputName());
			logicalInput.wasPressed = true;
			logicalInput.isDown = true;
		}
		nextPhysical.addPress(mouseButtonPressed);
	}
	
	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		PhysicalInput mouseButtonReleased = MouseButton.get(e.getButton());
		if (bindings.maps(mouseButtonReleased)) {
			LogicalInput logicalInput = next.getLogicalInputByName(bindings.get(mouseButtonReleased).getLogicalInputName());
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
		nextPhysical.setMousePosition(e.getX() / scale, e.getY() / scale);
	}
}


