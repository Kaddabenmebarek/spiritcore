package com.idorsia.research.spirit.core.constants;

import java.awt.Color;

public enum Status {
	//Default status
	INLAB("Available", true, Color.BLACK, null),

	//Normal status
	PLANNED("Planned", false, new Color(0, 0, 120), new Color(180, 180, 255)),
	STORED("Stored", true, Color.BLACK, new Color(180, 255, 180)),
	INPREP("In Preparation", true, new Color(0, 0, 150), new Color(180, 255, 180)),
	TRANSFERRED("Transferred", true, new Color(0, 0, 150), new Color(180, 180, 255)),
	USEDUP("Used Up", true, new Color(120, 70, 0), new Color(255, 180, 255)),
	LOWVOL("Low Volume", true, new Color(120, 70, 0), new Color(160, 160, 80)),
	PROCESSED("Processed", true, new Color(0, 0, 150), new Color(180, 180, 255)),

	//no more available status
	TRASHED ("Disposed", false, new Color(180, 0, 0), new Color(255, 80, 80)),
	NECROPSY("Necropsied", false, new Color(100, 70, 0), new Color(255, 225, 225)),

	//problematic status
	DEAD("Found Dead", false, new Color(100, 0, 0), new Color(255, 100, 100)),
	KILLED("Killed", false, new Color(100, 0, 0), new Color(255, 100, 100)),
	;

	private final String name;
	private final boolean available;
	private final Color foreground;
	private final Color background;

	private Status(String name, boolean available, Color foreground, Color background) {
		this.name = name;
		this.available = available;
		this.foreground = foreground;
		this.background = background;
	}

	public boolean isAvailable() {
		return available;
	}

	public String getName() {
		return name;
	}

	public Color getForeground() {
		return foreground;
	}

	public Color getBackground() {
		return background;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public static Status[] getAllowedStatus() {
		Status[] res = new Status[Status.values().length+1];
		res[0] = null;
		System.arraycopy(Status.values(), 0, res, 1, Status.values().length);
		return res;
	}
}
