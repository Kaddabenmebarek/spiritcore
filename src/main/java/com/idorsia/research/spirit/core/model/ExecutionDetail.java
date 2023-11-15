package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class ExecutionDetail implements Serializable, IObject {

	private static final long serialVersionUID = -8159809114788593149L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer phaselinkId;
	private Integer assignmentId;
	private Long deviation;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public ExecutionDetail() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPhaselinkId() {
		return phaselinkId;
	}

	public void setPhaselinkId(Integer phaselinkId) {
		this.phaselinkId = phaselinkId;
	}

	public Integer getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(Integer assignmentId) {
		this.assignmentId = assignmentId;
	}

	public Long getDeviation() {
		return deviation;
	}

	public void setDeviation(Long deviation) {
		this.deviation = deviation;
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
		result = prime * result + ((assignmentId == null) ? 0 : assignmentId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((deviation == null) ? 0 : deviation.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((phaselinkId == null) ? 0 : phaselinkId.hashCode());
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
		ExecutionDetail other = (ExecutionDetail) obj;
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
		if (deviation == null) {
			if (other.deviation != null)
				return false;
		} else if (!deviation.equals(other.deviation))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (phaselinkId == null) {
			if (other.phaselinkId != null)
				return false;
		} else if (!phaselinkId.equals(other.phaselinkId))
			return false;
		return true;
	}


}
