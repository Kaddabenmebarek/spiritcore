package com.idorsia.research.spirit.core.model;

import com.idorsia.research.spirit.core.constants.Constants;

public class BarcodeSequence {
	
	private Integer id = Constants.NEWTRANSIENTID;

	private String type;

	private String lastBarcode;

	private String category;

	public BarcodeSequence() {}

	public BarcodeSequence(String cat, String type, String lastBarcode) {
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

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	@Override
	public String toString() {
		return type + ">" + lastBarcode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastBarcode == null) ? 0 : lastBarcode.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BarcodeSequence other = (BarcodeSequence) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastBarcode == null) {
			if (other.lastBarcode != null)
				return false;
		} else if (!lastBarcode.equals(other.lastBarcode))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
}
