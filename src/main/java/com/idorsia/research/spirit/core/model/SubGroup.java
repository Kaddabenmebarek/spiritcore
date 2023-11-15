package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class SubGroup implements Serializable, IObject {

	private static final long serialVersionUID = -1704883500499898921L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer groupId;
	private Integer randofromgroupId;
	private Integer randofromsubgroupId;
	private Date creDate;
	private Date updDate;
	private String creUser;
	private String updUser;
	private Integer no;
	private String name;

	public SubGroup() {
		super();
	}

	public SubGroup(Integer id, Integer groupId) {
		super();
		this.id = id;
		this.groupId = groupId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public Integer getRandofromgroupId() {
		return randofromgroupId;
	}

	public void setRandofromgroupId(Integer randofromgroupId) {
		this.randofromgroupId = randofromgroupId;
	}

	public Integer getRandofromsubgroupId() {
		return randofromsubgroupId;
	}

	public void setRandofromsubgroupId(Integer randofromsubgroupId) {
		this.randofromsubgroupId = randofromsubgroupId;
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

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((no == null) ? 0 : no.hashCode());
		result = prime * result + ((randofromgroupId == null) ? 0 : randofromgroupId.hashCode());
		result = prime * result + ((randofromsubgroupId == null) ? 0 : randofromsubgroupId.hashCode());
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
		SubGroup other = (SubGroup) obj;
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
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (no == null) {
			if (other.no != null)
				return false;
		} else if (!no.equals(other.no))
			return false;
		if (randofromgroupId == null) {
			if (other.randofromgroupId != null)
				return false;
		} else if (!randofromgroupId.equals(other.randofromgroupId))
			return false;
		if (randofromsubgroupId == null) {
			if (other.randofromsubgroupId != null)
				return false;
		} else if (!randofromsubgroupId.equals(other.randofromsubgroupId))
			return false;
		return true;
	}


}
