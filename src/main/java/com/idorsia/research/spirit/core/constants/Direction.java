package com.idorsia.research.spirit.core.constants;

import javax.swing.ImageIcon;

public enum Direction {
	DEFAULT("Default", "default-dir.png"),
	LEFT_RIGHT("Left->Right", "left-right.png"),
	TOP_BOTTOM("Top->Bottom", "top-bottom.png"),
	PATTERN("Keep Pattern", null);

	private String name;
	private ImageIcon img;

	private Direction(String name, String img) {
		this.name = name;
		if(img!=null) {
			try {
				this.img = new ImageIcon(Direction.class.getResource(img));
			} catch(Exception e) {
				this.img = null;
				e.printStackTrace();
			}
		}

	}

	public String getName() {
		return name;
	}

	public ImageIcon getImage() {
		return img;
	}

	@Override
	public String toString() {
		return getName();
	}
}
