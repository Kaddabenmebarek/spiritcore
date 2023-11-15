package com.idorsia.research.spirit.core.constants;

public enum LocationLabeling {
	NONE("None", Direction.LEFT_RIGHT),
	NUM("1,2,...", Direction.LEFT_RIGHT),
	NUM_I("1,2 (Vertical)", Direction.TOP_BOTTOM),
	ALPHA("A1,A2,A3,...", Direction.LEFT_RIGHT);
	
	private final String name;	
	private final Direction defaultDirection;	
	
	private LocationLabeling(String name, Direction defaultDirection) {
		this.name = name;
		this.defaultDirection = defaultDirection;
	}
	
	public String getName() {
		return name;
	}
	
	public Direction getDefaultDirection() {
		return defaultDirection;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	public static LocationLabeling get(String toStringRepresentation) {
		for (LocationLabeling l : values()) {
			if(l.getName().equalsIgnoreCase(toStringRepresentation)) return l;
		}
		return null;
	}
}
