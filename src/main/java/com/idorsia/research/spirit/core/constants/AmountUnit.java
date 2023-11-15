package com.idorsia.research.spirit.core.constants;

public enum AmountUnit {
	VOL_ML("Volume", "ml"),
	VOL_UL("Volume", "ul"),
	WEIGHT_G("Weight", "g"),
	M_CELLS("Million Cells", "MCells");
	
	private final String name;
	private final String unit;
	
	private AmountUnit(String name, String unit) {
		this.name = name;
		this.unit = unit;
	}		
	
	public String getName() {return name;}		
	public String getUnit() {return unit;}				
	public String getNameUnit() {return name + " [" + unit + "]";}
	
	@Override
	public String toString() {
		return getNameUnit();
	}
}
