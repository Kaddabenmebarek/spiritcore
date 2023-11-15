package com.idorsia.research.spirit.core.constants;

public enum Category {
	BIOSAMPLE("BIOSAMPLE", 0),
	LOCATION("LOCATION", 1),
	CONTAINER("CONTAINER", 2);

	private final String name;
	private final Integer cat;

	private Category(String name, Integer cat) {
		this.name = name;
		this.cat = cat;
	}
	
	@Override
	public String toString() {
		return name;
	}
	public String getName() {
		return name;
	}

	public Integer getCat() {
		return cat;
	}

}