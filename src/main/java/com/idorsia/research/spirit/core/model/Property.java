package com.idorsia.research.spirit.core.model;

import java.io.Serializable;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;
import com.idorsia.research.spirit.core.util.DataUtils;

public class Property implements Serializable, IObject {
	
	private static final long serialVersionUID = 4609292605049943549L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String name;
	private Integer mandatory;
	
	public Property() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMandatory() {
		return mandatory;
	}

	public void setMandatory(Integer mandatory) {
		this.mandatory = mandatory;
	}
	
	public Boolean isMandatory() {
		return mandatory==1;
	}
	
	public void setMandatory(Boolean mandatory) {
		this.mandatory=DataUtils.booleanToInt(mandatory);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((mandatory == null) ? 0 : mandatory.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Property other = (Property) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (mandatory == null) {
			if (other.mandatory != null)
				return false;
		} else if (!mandatory.equals(other.mandatory))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
}
