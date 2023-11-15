package com.idorsia.research.spirit.core.constants;

import java.awt.Color;

public enum BiotypeCategory {
	LIVING("Living", new Color(255, 255, 140)),
	SOLID("Solid sample", new Color(255, 255, 220)),
	LIQUID("Liquid sample", new Color(245, 245, 200)),
	PURIFIED("Purified sample", new Color(235, 225, 200)),
	FORMULATION("Formulation", new Color(240, 225, 255)),
	LIBRARY("Library (abstract / used as references)", new Color(240, 240, 240));

	private final String name;
	private final Color background;

	private BiotypeCategory(String name, Color background) {
		this.name = name;
		this.background = background;
	}
	@Override
	public String toString() {
		return name;
	}
	public String getName() {
		return name;
	}

	public String getShortName() {
		int index = name.indexOf(' ');
		return index>0? name.substring(0, index): name;
	}

	public Color getBackground() {
		return background;
	}
}
