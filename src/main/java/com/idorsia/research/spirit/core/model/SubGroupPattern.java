package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class SubGroupPattern implements Serializable, IObject {

	private static final long serialVersionUID = 3561661363816475507L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer actionpatternId;
	private Integer subgroupId;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public SubGroupPattern() {
		super();
	}

	public SubGroupPattern(Integer id, Integer actionpatternId, Integer subgroupId) {
		super();
		this.id = id;
		this.actionpatternId = actionpatternId;
		this.subgroupId = subgroupId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getActionpatternId() {
		return actionpatternId;
	}

	public void setActionpatternId(Integer actionpatternId) {
		this.actionpatternId = actionpatternId;
	}

	public Integer getSubgroupId() {
		return subgroupId;
	}

	public void setSubgroupId(Integer subgroupId) {
		this.subgroupId = subgroupId;
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
		result = prime * result + ((actionpatternId == null) ? 0 : actionpatternId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((subgroupId == null) ? 0 : subgroupId.hashCode());
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
		SubGroupPattern other = (SubGroupPattern) obj;
		if (actionpatternId == null) {
			if (other.actionpatternId != null)
				return false;
		} else if (!actionpatternId.equals(other.actionpatternId))
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
		if (subgroupId == null) {
			if (other.subgroupId != null)
				return false;
		} else if (!subgroupId.equals(other.subgroupId))
			return false;
		return true;
	}

}
