package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class AssayResult implements Serializable, IObject {

	private static final long serialVersionUID = -1910523208155146657L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String comments;
	private Date creDate;
	private String creUser;
	private String elb;
	private Integer quality;
	private Date updDate;
	private String updUser;
	private Integer biosampleId;
	private Integer phaseId;
	private Integer assayId;
	private Integer studyId;
	private Timestamp  executionDate;

	public AssayResult() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
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

	public Integer getAssayId() {
		return assayId;
	}

	public void setAssayId(Integer assayId) {
		this.assayId = assayId;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Timestamp getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Timestamp executionDate) {
		this.executionDate = executionDate;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assayId == null) ? 0 : assayId.hashCode());
		result = prime * result + ((biosampleId == null) ? 0 : biosampleId.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((elb == null) ? 0 : elb.hashCode());
		result = prime * result + ((executionDate == null) ? 0 : executionDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((phaseId == null) ? 0 : phaseId.hashCode());
		result = prime * result + ((quality == null) ? 0 : quality.hashCode());
		result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
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
		AssayResult other = (AssayResult) obj;
		if (assayId == null) {
			if (other.assayId != null)
				return false;
		} else if (!assayId.equals(other.assayId))
			return false;
		if (biosampleId == null) {
			if (other.biosampleId != null)
				return false;
		} else if (!biosampleId.equals(other.biosampleId))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
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
		if (elb == null) {
			if (other.elb != null)
				return false;
		} else if (!elb.equals(other.elb))
			return false;
		if (executionDate == null) {
			if (other.executionDate != null)
				return false;
		} else if (!executionDate.equals(other.executionDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (phaseId == null) {
			if (other.phaseId != null)
				return false;
		} else if (!phaseId.equals(other.phaseId))
			return false;
		if (quality == null) {
			if (other.quality != null)
				return false;
		} else if (!quality.equals(other.quality))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		return true;
	}
	
	
}
