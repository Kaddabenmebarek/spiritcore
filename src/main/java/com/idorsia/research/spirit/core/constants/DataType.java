package com.idorsia.research.spirit.core.constants;

public enum DataType {	
	ALPHA("Alphanumeric", null),
	AUTO("Autocomplete", null),
	NUMBER("Numeric", null),		
	LIST("OneChoice", "List of options"),
	MULTI("MultiChoice", "List of options"),
	DATE("Date/Time", null),	
	D_FILE("Single File", null),
	FILES("Multiple Files", null),
	LARGE("LargeText", null),
	FORMULA("Formula", "Formula"),	
	BIOSAMPLE("Biosample", "Biotype"),
	OBSERVATION("Observation", "Observation, Localisation, Severity"),
	COMPOUND("Compound", null),
	BOOLEAN("Boolean", "Default value")
	;
	
	private final String name;
	private final String parametersDescription;
	
	private DataType(String name, String parametersDescription) {
		this.name = name;
		this.parametersDescription = parametersDescription;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isDocument() {
		return this==DataType.D_FILE || this==FILES;
	}
	
	public String getParametersDescription() {
		return parametersDescription;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
