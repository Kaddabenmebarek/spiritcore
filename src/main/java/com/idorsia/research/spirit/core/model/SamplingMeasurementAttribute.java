package com.idorsia.research.spirit.core.model;

import java.util.Date;

import com.idorsia.research.spirit.core.dto.IObject;

public class SamplingMeasurementAttribute implements IObject{

	private Integer id;
	private Integer assayAttributeId;
	private Integer samplingMeasurementId;
	private String value;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAssayAttributeId() {
		return assayAttributeId;
	}
	public void setAssayAttributeId(Integer assayAttributeId) {
		this.assayAttributeId = assayAttributeId;
	}
	public Integer getSamplingMeasurementId() {
		return samplingMeasurementId;
	}
	public void setSamplingMeasurementId(Integer samplingMeasurementId) {
		this.samplingMeasurementId = samplingMeasurementId;
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
		result = prime * result + ((assayAttributeId == null) ? 0 : assayAttributeId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((samplingMeasurementId == null) ? 0 : samplingMeasurementId.hashCode());
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
		SamplingMeasurementAttribute other = (SamplingMeasurementAttribute) obj;
		if (assayAttributeId == null) {
			if (other.assayAttributeId != null)
				return false;
		} else if (!assayAttributeId.equals(other.assayAttributeId))
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
		if (samplingMeasurementId == null) {
			if (other.samplingMeasurementId != null)
				return false;
		} else if (!samplingMeasurementId.equals(other.samplingMeasurementId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
