package com.idorsia.research.spirit.core.model;

import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class SubGroupBiotypeMetadataValue implements IObject {
	
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer subgroupId;
	private Integer biotypeMetadataValueId;
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
	public Integer getSubgroupId() {
		return subgroupId;
	}
	public void setSubgroupId(Integer subgroupId) {
		this.subgroupId = subgroupId;
	}
	public Integer getBiotypeMetadataValueId() {
		return biotypeMetadataValueId;
	}
	public void setBiotypeMetadataValueId(Integer biotypeMetadataValueId) {
		this.biotypeMetadataValueId = biotypeMetadataValueId;
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
		result = prime * result + ((biotypeMetadataValueId == null) ? 0 : biotypeMetadataValueId.hashCode());
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
		SubGroupBiotypeMetadataValue other = (SubGroupBiotypeMetadataValue) obj;
		if (biotypeMetadataValueId == null) {
			if (other.biotypeMetadataValueId != null)
				return false;
		} else if (!biotypeMetadataValueId.equals(other.biotypeMetadataValueId))
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
