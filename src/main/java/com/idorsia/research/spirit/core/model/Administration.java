package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Administration implements Serializable, IObject {

	private static final long serialVersionUID = 654556084893670910L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer namedTreatmentId;
	private Integer biosampleId;
	private Integer phaseId;
	private String batchId;
	private String batchType;
	private Float effectiveamount;
	private Integer effectiveamountunitId;
	private Date executiondate;
	private String elb;
	private String admComment;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public Administration() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNamedTreatmentId() {
		return namedTreatmentId;
	}

	public void setNamedTreatmentId(Integer namedTreatmentId) {
		this.namedTreatmentId = namedTreatmentId;
	}

	public Integer getBiosampleId() {
		return biosampleId;
	}

	public void setBiosampleId(Integer biosampleId) {
		this.biosampleId = biosampleId;
	}

	public Integer getPhaseId() {
		return phaseId;
	}

	public void setPhaseId(Integer phaseId) {
		this.phaseId = phaseId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public Float getEffectiveamount() {
		return effectiveamount;
	}

	public void setEffectiveamount(Float effectiveamount) {
		this.effectiveamount = effectiveamount;
	}

	public Integer getEffectiveamountunitId() {
		return effectiveamountunitId;
	}

	public void setEffectiveamountunitId(Integer effectiveamountunitId) {
		this.effectiveamountunitId = effectiveamountunitId;
	}

	public Date getExecutiondate() {
		return executiondate;
	}

	public void setExecutiondate(Date executiondate) {
		this.executiondate = executiondate;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public String getAdmComment() {
		return admComment;
	}

	public void setAdmComment(String admComment) {
		this.admComment = admComment;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Administration other = (Administration) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (admComment == null) {
			if (other.admComment != null)
				return false;
		} else if (!admComment.equals(other.admComment))
			return false;
		if (batchId == null) {
			if (other.batchId != null)
				return false;
		} else if (!batchId.equals(other.batchId))
			return false;
		if (batchType == null) {
			if (other.batchType != null)
				return false;
		} else if (!batchType.equals(other.batchType))
			return false;
		if (effectiveamount == null) {
			if (other.effectiveamount != null)
				return false;
		} else if (!effectiveamount.equals(other.effectiveamount))
			return false;
		if (effectiveamountunitId == null) {
			if (other.effectiveamountunitId != null)
				return false;
		} else if (!effectiveamountunitId.equals(other.effectiveamountunitId))
			return false;
		if (elb == null) {
			if (other.elb != null)
				return false;
		} else if (!elb.equals(other.elb))
			return false;
		if (namedTreatmentId == null) {
			if (other.namedTreatmentId != null)
				return false;
		} else if (!namedTreatmentId.equals(other.namedTreatmentId))
			return false;
		return true;
	}

}
