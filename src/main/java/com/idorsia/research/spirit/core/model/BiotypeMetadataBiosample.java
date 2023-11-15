package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class BiotypeMetadataBiosample implements Serializable, IObject {

	private static final long serialVersionUID = -381689084388389836L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer biosampleId;
	private Integer metadataId;
	private String value;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public BiotypeMetadataBiosample() {
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getBiosampleId() {
		return biosampleId;
	}
	public void setBiosampleId(Integer biosampleId) {
		this.biosampleId = biosampleId;
	}
	public Integer getMetadataId() {
		return metadataId;
	}
	public void setMetadataId(Integer metadataId) {
		this.metadataId = metadataId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biosampleId == null) ? 0 : biosampleId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((metadataId == null) ? 0 : metadataId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		BiotypeMetadataBiosample other = (BiotypeMetadataBiosample) obj;
		if (biosampleId == null) {
			if (other.biosampleId != null)
				return false;
		} else if (!biosampleId.equals(other.biosampleId))
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (metadataId == null) {
			if (other.metadataId != null)
				return false;
		} else if (!metadataId.equals(other.metadataId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
