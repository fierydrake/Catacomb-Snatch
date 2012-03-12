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
		
		public String toString() {
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
	private Map<PhysicalInput, String> physicalToLogicalInputMapping = new HashMap<PhysicalInput, String>();
	private Map<String, List<PhysicalInput>> logicalToPhysicalInputMappings = new HashMap<String, List<PhysicalInput>>();

	public void map(LogicalInput logical, PhysicalInput physical) {
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
	
	public boolean maps(PhysicalInput physicalInput) {
		return physicalToLogicalInputMapping.containsKey(physicalInput);
	}
	
	public InputBinding getBinding(LogicalInput logicalInput) {
		return new InputBinding(logicalInput.name, logicalToPhysicalInputMappings.get(logicalInput.name));
	}
	
	public InputBinding getBinding(PhysicalInput physicalInput) {
		String logicalInputName = physicalToLogicalInputMapping.get(physicalInput); 
		return new InputBinding(logicalInputName, logicalToPhysicalInputMappings.get(logicalInputName));
	}
}
