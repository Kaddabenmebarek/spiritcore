package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class ResultAssignment implements Serializable, IObject {

	private static final long serialVersionUID = 9204873456938350358L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer assignmentId;
	private Integer assayResultId;
	private Date creDate;
	private Date updDate;
	private String updUser;
	private String creUser;
	
	public ResultAssignment() {
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAssignmentId() {
		return assignmentId;
	}
	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}
	public Integer getAssayResultId() {
		return assayResultId;
	}
	public void setAssayResultId(Integer resultId) {
		this.assayResultId = resultId;
	}
	public Date getCreDate() {
		return creDate;
	}
	public void setCreDate(Date creDate) {
		this.creDate = creDate;
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
	public String getCreUser() {
		return creUser;
	}
	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((assayResultId == null) ? 0 : assayResultId.hashCode());
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
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
		ResultAssignment other = (ResultAssignment) obj;
		if (assayResultId == null) {
			if (other.assayResultId != null)
				return false;
		} else if (!assayResultId.equals(other.assayResultId))
			return false;
		if (assignmentId == null) {
			if (other.assignmentId != null)
				return false;
		} else if (!assignmentId.equals(other.assignmentId))
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
		return true;
	}
	
}
