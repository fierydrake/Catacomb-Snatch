package com.mojang.mojam.gameinput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.mojam.gameinput.LogicalInputs.LogicalInput;

/*
 * This class maintains mappings between physical inputs (like keys 
 * and mouse buttons) and logical inputs (like fire and movement). 
 * 
 * Each logical input can be mapped against up to 3 physical inputs.
 * This is an arbitrary limit.
 * 
 * TODO Should use InputBindings in the hash maps rather than
 *      creating them on the fly.
 */
public class InputBindings {
	public class InputBinding {
		private String logicalInputName;
		private List<PhysicalInput> physicalInputs;
		
		public InputBinding(String logicalInputName, List<PhysicalInput> physicalInputs) {
			this.logicalInputName = logicalInputName;
			this.physicalInputs = physicalInputs;
		}
		
		public String getLogicalInputName() { return logicalInputName; }
		public List<PhysicalInput> getPhysicalInputs() { return physicalInputs; }
		
	    @Override
		public String toString() {
			if (physicalInputs.size() == 0) return "NONE"; // TODO Translate
			StringBuilder s = new StringBuilder();
			for (PhysicalInput physicalInput : physicalInputs) {
				s.append(physicalInput.getDisplayName());
				s.append(",");
			}
			s.setLength(s.length() - 1);
			return s.toString();
		}
	}
	
	public static final int MAX_PHYSICAL_PER_LOGICAL = 3;
	private List<String> logicalInputNames = new ArrayList<String>(); /* Keep an ordered list */
	private Map<PhysicalInput, String> physicalToLogicalInputMapping = new HashMap<PhysicalInput, String>();
	private Map<String, List<PhysicalInput>> logicalToPhysicalInputMappings = new HashMap<String, List<PhysicalInput>>();

	public void map(LogicalInput logical, PhysicalInput physical) {
		map(logical.name, physical);
	}
		
	public void map(String logicalInputName, PhysicalInput physical) {
		/* Remove any existing mappings to this physical input */
		clearMappingOf(physical);
		
		/* Ensure this logical input is keyed in the map */
		if (!logicalToPhysicalInputMappings.containsKey(logicalInputName)) {
			logicalInputNames.add(logicalInputName);
			logicalToPhysicalInputMappings.put(logicalInputName, new ArrayList<PhysicalInput>());
		}
		
		/* Cap the number of bound physical inputs for this logical input */
		if (logicalToPhysicalInputMappings.get(logicalInputName).size() == MAX_PHYSICAL_PER_LOGICAL) {
			logicalToPhysicalInputMappings.remove(0);
		}

		/* Add the new mapping */
		logicalToPhysicalInputMappings.get(logicalInputName).add(physical);
		physicalToLogicalInputMapping.put(physical, logicalInputName);
	}
	
	/* Remove all physical inputs from the mapping for logicalInputName */
	public void unmap(String logicalInputName) {
		List<PhysicalInput> physicalInputs = logicalToPhysicalInputMappings.get(logicalInputName);
		if (physicalInputs != null) {
			physicalInputs.clear();
		}
	}
	
	private void clearMappingOf(PhysicalInput physical) {
		/* If this physical key is mapped to a logical key... */
		if (physicalToLogicalInputMapping.containsKey(physical)) {
			String logicalInputName = physicalToLogicalInputMapping.get(physical);
			/* ...remove the physical key from that mapping*/
			List<PhysicalInput> physicalInputs = logicalToPhysicalInputMappings.get(logicalInputName);
			physicalInputs.remove(physical);
			physicalToLogicalInputMapping.remove(physical);
		}
	}
	
	public boolean maps(PhysicalInput physicalInput) {
		return physicalToLogicalInputMapping.containsKey(physicalInput);
	}
	
	public InputBinding get(LogicalInput logicalInput) {
		return get(logicalInput.name);
	}
	
	public InputBinding get(PhysicalInput physicalInput) {
		String logicalInputName = physicalToLogicalInputMapping.get(physicalInput); 
		return new InputBinding(logicalInputName, logicalToPhysicalInputMappings.get(logicalInputName));
	}
	
	public InputBinding get(String logicalInputName) {
		return new InputBinding(logicalInputName, logicalToPhysicalInputMappings.get(logicalInputName));
	}
	
	public List<InputBinding> getAll() {
		List<InputBinding> bindings = new ArrayList<InputBinding>();
		for (String logicalInputName : logicalInputNames) {
			bindings.add(get(logicalInputName));
		}
		return bindings;
	}
}
