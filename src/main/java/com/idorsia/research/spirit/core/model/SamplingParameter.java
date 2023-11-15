package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class SamplingParameter implements Serializable, IObject {

	private static final long serialVersionUID = 1299357775025438934L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer biotypemetadataId;
	private Integer samplingId;
	private String value;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public SamplingParameter() {
		super();
	}

	public SamplingParameter(Integer id, Integer biotypemetadataId, Integer samplingId) {
		super();
		this.id = id;
		this.biotypemetadataId = biotypemetadataId;
		this.samplingId = samplingId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBiotypemetadataId() {
		return biotypemetadataId;
	}

	public void setBiotypemetadataId(Integer biotypemetadataId) {
		this.biotypemetadataId = biotypemetadataId;
	}

	public Integer getSamplingId() {
		return samplingId;
	}

	public void setSamplingId(Integer samplingId) {
		this.samplingId = samplingId;
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
		result = prime * result + ((biotypemetadataId == null) ? 0 : biotypemetadataId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((samplingId == null) ? 0 : samplingId.hashCode());
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
		SamplingParameter other = (SamplingParameter) obj;
		if (biotypemetadataId == null) {
			if (other.biotypemetadataId != null)
				return false;
		} else if (!biotypemetadataId.equals(other.biotypemetadataId))
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
		if (samplingId == null) {
			if (other.samplingId != null)
				return false;
		} else if (!samplingId.equals(other.samplingId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
