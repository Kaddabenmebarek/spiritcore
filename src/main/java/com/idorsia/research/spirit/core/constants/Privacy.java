package com.idorsia.research.spirit.core.constants;

import java.awt.Color;

public enum Privacy {
	INHERITED("Inherited", "Same as the parent", Color.BLACK, Color.WHITE),
	PUBLIC("Public", null, new Color(0,120,0), new Color(150, 220, 100)),
	PROTECTED("Protected", "Hidden", new Color(100,80,0),new Color(200, 200, 100)),
	PRIVATE("Private", "Not searchable", new Color(160,0,0),new Color(220, 150, 100));


	private final String name;
	private final String comments;
	private final Color fgColor;
	private final Color bgColor;

	private Privacy(String name, String comments, Color fgColor, Color bgColor) {
		this.name = name;
		this.comments = comments;
		this.fgColor = fgColor;
		this.bgColor = bgColor;
	}

	public String getName() {
		return name;
	}

	public Color getFgColor() {
		return fgColor;
	}

	public Color getBgColor() {
		return bgColor;
	}

	public String getComments() {
		return comments;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Privacy get(String toStringRepresentation) {
		if(toStringRepresentation==null) return null;
		for (Privacy l : values()) {
			if(l.getName().equalsIgnoreCase(toStringRepresentation)) return l;
		}
		return null;
	}
}
