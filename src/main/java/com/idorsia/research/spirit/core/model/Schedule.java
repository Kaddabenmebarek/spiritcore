package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Schedule implements Serializable, IObject{

	private static final long serialVersionUID = 3217720923788386459L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer startDate;
	private Long lastPhase;
	private String rRule;
	private String timePoints;
	private Date creDate;
	private Date updDate;
	private String creUser;
	private String updUser;

	public Schedule() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStartDate() {
		return startDate;
	}

	public void setStartDate(Integer startDate) {
		this.startDate = startDate;
	}

	public Long getLastPhase() {
		return lastPhase;
	}

	public void setLastPhase(Long lastPhase) {
		this.lastPhase = lastPhase;
	}

	public String getrRule() {
		return rRule;
	}

	public void setrRule(String rRule) {
		this.rRule = rRule;
	}

	public String getTimePoints() {
		return timePoints;
	}

	public void setTimePoints(String timePoints) {
		this.timePoints = timePoints;
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

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
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
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastPhase == null) ? 0 : lastPhase.hashCode());
		result = prime * result + ((rRule == null) ? 0 : rRule.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((timePoints == null) ? 0 : timePoints.hashCode());
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
		Schedule other = (Schedule) obj;
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
		if (lastPhase == null) {
			if (other.lastPhase != null)
				return false;
		} else if (!lastPhase.equals(other.lastPhase))
			return false;
		if (rRule == null) {
			if (other.rRule != null)
				return false;
		} else if (!rRule.equals(other.rRule))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (timePoints == null) {
			if (other.timePoints != null)
				return false;
		} else if (!timePoints.equals(other.timePoints))
			return false;
		return true;
	}

}
