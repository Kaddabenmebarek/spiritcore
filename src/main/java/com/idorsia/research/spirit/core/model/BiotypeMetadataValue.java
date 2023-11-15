package com.idorsia.research.spirit.core.model;

import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class BiotypeMetadataValue implements IObject {

	private Integer id = Constants.NEWTRANSIENTID;
    private String value;
    private Integer biotypemetadataId;
    private String creuser;
    private Date credate;
    private String upduser;
    private Date upddate;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getBiotypemetadataId() {
		return biotypemetadataId;
	}
	public void setBiotypemetadataId(Integer biotypemetadataId) {
		this.biotypemetadataId = biotypemetadataId;
	}
	public Date getUpddate() {
		return upddate;
	}
	public void setUpddate(Date updDate) {
		this.upddate = updDate;
	}
	public String getCreuser() {
		return creuser;
	}
	public void setCreuser(String creUser) {
		this.creuser = creUser;
	}
	public Date getCredate() {
		return credate;
	}
	public void setCredate(Date credate) {
		this.credate = credate;
	}
	public String getUpduser() {
		return upduser;
	}
	public void setUpduser(String updUser) {
		this.upduser = updUser;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biotypemetadataId == null) ? 0 : biotypemetadataId.hashCode());
		result = prime * result + ((credate == null) ? 0 : credate.hashCode());
		result = prime * result + ((creuser == null) ? 0 : creuser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		BiotypeMetadataValue other = (BiotypeMetadataValue) obj;
		if (biotypemetadataId == null) {
			if (other.biotypemetadataId != null)
				return false;
		} else if (!biotypemetadataId.equals(other.biotypemetadataId))
			return false;
		if (credate == null) {
			if (other.credate != null)
				return false;
		} else if (!credate.equals(other.credate))
			return false;
		if (creuser == null) {
			if (other.creuser != null)
				return false;
		} else if (!creuser.equals(other.creuser))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
