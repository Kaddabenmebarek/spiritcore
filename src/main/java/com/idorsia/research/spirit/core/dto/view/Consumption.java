package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Consumption implements Serializable {

	private static final long serialVersionUID = -8118307521432497205L;
	private final boolean water;
	public Date fromDate;
	public Date toDate;
	public BigDecimal value;
	public String formula;
	
	public Consumption(boolean water) {
		this.water = water;
	}
	public boolean isWater() {
		return water;
	}

}
