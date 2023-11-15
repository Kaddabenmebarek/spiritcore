package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class StagePattern implements Serializable, IObject {

	private static final long serialVersionUID = 6597736064780379393L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer actionpatternId;
	private Integer stageId;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public StagePattern() {
		super();
	}

	public StagePattern(Integer id, Integer actionpatternId, Integer stageId) {
		super();
		this.id = id;
		this.actionpatternId = actionpatternId;
		this.stageId = stageId;
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

	public Integer getStageId() {
		return stageId;
	}

	public void setStageId(Integer stageId) {
		this.stageId = stageId;
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
		result = prime * result + ((actionpatternId == null) ? 0 : actionpatternId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((stageId == null) ? 0 : stageId.hashCode());
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
		StagePattern other = (StagePattern) obj;
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
		if (stageId == null) {
			if (other.stageId != null)
				return false;
		} else if (!stageId.equals(other.stageId))
			return false;
		return true;
	}

}
