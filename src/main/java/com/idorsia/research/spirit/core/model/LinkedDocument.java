package com.idorsia.research.spirit.core.model;

import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class LinkedDocument implements IObject {

	private Integer id = Constants.NEWTRANSIENTID;
	private Integer biosampleId;
	private Integer linkeddocumentId;
	private Integer biotypemetadataId;
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
	public Integer getBiosampleId() {
		return biosampleId;
	}
	public void setBiosampleId(Integer biosampleId) {
		this.biosampleId = biosampleId;
	}
	public Integer getLinkeddocumentId() {
		return linkeddocumentId;
	}
	public void setLinkeddocumentId(Integer linkeddocumentId) {
		this.linkeddocumentId = linkeddocumentId;
	}
	public Integer getBiotypemetadataId() {
		return biotypemetadataId;
	}
	public void setBiotypemetadataId(Integer biotypemetadataId) {
		this.biotypemetadataId = biotypemetadataId;
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
		result = prime * result + ((biosampleId == null) ? 0 : biosampleId.hashCode());
		result = prime * result + ((biotypemetadataId == null) ? 0 : biotypemetadataId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((linkeddocumentId == null) ? 0 : linkeddocumentId.hashCode());
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
		LinkedDocument other = (LinkedDocument) obj;
		if (biosampleId == null) {
			if (other.biosampleId != null)
				return false;
		} else if (!biosampleId.equals(other.biosampleId))
			return false;
		if (biotypemetadataId == null) {
			if (other.biotypemetadataId != null)
				return false;
		} else if (!biotypemetadataId.equals(other.biotypemetadataId))
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
		if (linkeddocumentId == null) {
			if (other.linkeddocumentId != null)
				return false;
		} else if (!linkeddocumentId.equals(other.linkeddocumentId))
			return false;
		return true;
	}
}
