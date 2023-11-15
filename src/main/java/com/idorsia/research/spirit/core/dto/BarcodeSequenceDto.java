package com.idorsia.research.spirit.core.dto;

import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.Category;
import com.idorsia.research.spirit.core.constants.Constants;

public class BarcodeSequenceDto implements IObject, Serializable {
	
	private static final long serialVersionUID = 4955896774415551849L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String type;
	private String lastBarcode;

	private Category category = Category.BIOSAMPLE;

	public BarcodeSequenceDto() {}

	public BarcodeSequenceDto(Category cat, String type, String lastBarcode) {
		this.category = cat;
		this.type = type;
		this.lastBarcode = lastBarcode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getLastBarcode() {
		return lastBarcode;
	}
	public void setLastBarcode(String lastBarcode) {
		this.lastBarcode = lastBarcode;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Category getCategory() {
		return category;
	}

	@Override
	public String toString() {
		return type + ">" + lastBarcode;
	}
}
