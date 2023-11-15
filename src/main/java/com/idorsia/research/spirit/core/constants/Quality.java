package com.idorsia.research.spirit.core.constants;

import java.awt.Color;

public enum Quality {
	BOGUS(0, "Bogus", new Color(255, 200, 200)), 
	QUESTIONABLE(1, "Questionable", new Color(255, 230, 170)),
	VALID(2, "Valid", null), 
	CONFIRMED(3, "Confirmed", new Color(200, 255, 200));

	private final Integer id;
	private final String name;
	private final Color background;

	private Quality(int id, String name, Color background) {
		this.id = id;
		this.name = name;
		this.background = background;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Color getBackground() {
		return background;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Quality get(String name) {
		for (Quality q : values()) {
			if (q.name().equals(name))
				return q;
		}
		return null;
	}

	public static Quality getById(Integer id) {
		for (Quality q : values()) {
			if (q.getId().equals(id))
				return q;
		}
		return null;
	}
}