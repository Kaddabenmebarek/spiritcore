package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;

import com.actelion.research.util.CompareUtils;
import com.idorsia.research.spirit.core.constants.AmountUnit;

public class Amount implements Comparable<Amount>, Serializable {
	private static final long serialVersionUID = 7064255084173151311L;
	private Double quantity;
	private AmountUnit unit;
	
	public Amount(Double quantity, AmountUnit unit) {
		if(unit==null) throw new IllegalArgumentException("Unit cannot be null");
		this.quantity = quantity;
		this.unit = unit;
	}
	

	public Double getQuantity() {
		return quantity;
	}
	
	public AmountUnit getUnit() {
		return unit;
	}
	
	@Override
	public String toString() {
		return quantity==null?"": quantity + unit.getUnit();
	}
	
	@Override
	public int compareTo(Amount a) {
		int c = unit.compareTo(a.unit);
		if(c!=0) return c;
		return CompareUtils.compare(quantity, a.quantity);
	}
	

}
