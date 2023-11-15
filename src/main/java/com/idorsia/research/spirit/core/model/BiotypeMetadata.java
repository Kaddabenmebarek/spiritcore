package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class BiotypeMetadata implements Serializable, IObject {

	private static final long serialVersionUID = -7736771497568147128L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer idx;
	private String name;
	private String parameters;
	private Integer required;
	private Integer biotypeId;
	private String description;
	private String datatype;
	private Integer hidefromdisplay;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public BiotypeMetadata() {
		super();
	}

	public BiotypeMetadata(Integer id, Integer idx, String name, Integer required, Integer biotypeId) {
		super();
		this.id = id;
		this.idx = idx;
		this.name = name;
		this.required = required;
		this.biotypeId = biotypeId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Integer getRequired() {
		return required;
	}

	public void setRequired(Integer required) {
		this.required = required;
	}

	public Integer getBiotypeId() {
		return biotypeId;
	}

	public void setBiotypeId(Integer biotypeId) {
		this.biotypeId = biotypeId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public Integer getHidefromdisplay() {
		return hidefromdisplay;
	}

	public void setHidefromdisplay(Integer hidefromdisplay) {
		this.hidefromdisplay = hidefromdisplay;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biotypeId == null) ? 0 : biotypeId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((hidefromdisplay == null) ? 0 : hidefromdisplay.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idx == null) ? 0 : idx.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((required == null) ? 0 : required.hashCode());
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
		BiotypeMetadata other = (BiotypeMetadata) obj;
		if (biotypeId == null) {
			if (other.biotypeId != null)
				return false;
		} else if (!biotypeId.equals(other.biotypeId))
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
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (hidefromdisplay == null) {
			if (other.hidefromdisplay != null)
				return false;
		} else if (!hidefromdisplay.equals(other.hidefromdisplay))
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
		return true;
	}
	
}
