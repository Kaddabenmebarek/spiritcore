package com.idorsia.research.spirit.core.model;

import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class AssayAttribute implements IObject {
	
	private Integer id = Constants.NEWTRANSIENTID;
	private String datatype;
	private Integer idx;
	private String outputType;
	private Integer required;
	private String name;
	private String unit;
	private Integer assayId;
	private String parameters;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public AssayAttribute() {
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public Integer getIdx() {
		return idx;
	}
	public void setIdx(Integer idx) {
		this.idx = idx;
	}
	public String getOutputType() {
		return outputType;
	}
	public void setOutputType(String output) {
		this.outputType = output;
	}
	public void setRequired(Integer required) {
		this.required = required;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Integer getAssayId() {
		return assayId;
	}

	public void setAssayId(Integer assayId) {
		this.assayId = assayId;
	}

	public Integer getRequired() {
		return required;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assayId == null) ? 0 : assayId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idx == null) ? 0 : idx.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((outputType == null) ? 0 : outputType.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((required == null) ? 0 : required.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		AssayAttribute other = (AssayAttribute) obj;
		if (assayId == null) {
			if (other.assayId != null)
				return false;
		} else if (!assayId.equals(other.assayId))
			return false;
		if (creDate == null) {
			if (other.creDate != null)
				return false;
		} else if (!creDate.equals(other.creDate))
			return false;
		if (creUser == null) {
			if (other.creUser != null)
				return false;
		} else if (!creUser.equals(other.creUser))
			return false;
		if (datatype == null) {
			if (other.datatype != null)
				return false;
		} else if (!datatype.equals(other.datatype))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (idx == null) {
			if (other.idx != null)
				return false;
		} else if (!idx.equals(other.idx))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (outputType == null) {
			if (other.outputType != null)
				return false;
		} else if (!outputType.equals(other.outputType))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (required == null) {
			if (other.required != null)
				return false;
		} else if (!required.equals(other.required))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}
	
	
}
