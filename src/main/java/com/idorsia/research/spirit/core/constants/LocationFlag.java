package com.idorsia.research.spirit.core.constants;

import javax.swing.Icon;

import com.actelion.research.util.ui.iconbutton.IconType;

public enum LocationFlag {
	GREEN("Green", IconType.GREEN_FLAG.getIcon()),
	ORANGE("Orange", IconType.ORANGE_FLAG.getIcon()),
	RED("Red", IconType.RED_FLAG.getIcon());

	private final String name;
	private final Icon icon;

	private LocationFlag(String name, Icon icon) {
		this.name = name;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return icon;
	}

	@Override
	public String toString() {
		return name;
	}
}
