package com.idorsia.research.spirit.core.constants;

public enum BiosampleDuplicateMethod {
	RETURN_ALL("Returns all having duplicate"), RETURN_OLDEST("Returns the oldests"),
	RETURN_NEWEST("Returns the newests"), RETURN_OLDEST_WITHOUT_RESULT("Returns The oldest, but keep all with results");

	private String name;

	private BiosampleDuplicateMethod(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
