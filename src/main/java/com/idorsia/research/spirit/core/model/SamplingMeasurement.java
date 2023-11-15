package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class SamplingMeasurement implements Serializable, IObject {

	private static final long serialVersionUID = -4579475419486525455L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer samplingId;
	private Integer assayId;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public SamplingMeasurement() {
		super();
	}

	public SamplingMeasurement(Integer id, Integer samplingId, Integer testId) {
		super();
		this.id = id;
		this.samplingId = samplingId;
		this.assayId = testId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSamplingId() {
		return samplingId;
	}

	public void setSamplingId(Integer samplingId) {
		this.samplingId = samplingId;
	}

	public Integer getAssayId() {
		return assayId;
	}

	public void setAssayId(Integer testId) {
		this.assayId = testId;
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
		result = prime * result + ((assayId == null) ? 0 : assayId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((samplingId == null) ? 0 : samplingId.hashCode());
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
		SamplingMeasurement other = (SamplingMeasurement) obj;
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
		return true;
	}

}
