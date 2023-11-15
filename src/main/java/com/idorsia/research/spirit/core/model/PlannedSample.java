package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class PlannedSample implements Serializable, IObject {

	private static final long serialVersionUID = 8038400335799589174L;
	
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer biosampleId;
	private String name;
	private Integer stageId;
	private Double weight;
	private String datalist;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	
	public PlannedSample() {
	}
	
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStageId() {
		return stageId;
	}
	public void setStageId(Integer stageId) {
		this.stageId = stageId;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getDatalist() {
		return datalist;
	}
	public void setDatalist(String datalist) {
		this.datalist = datalist;
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
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((datalist == null) ? 0 : datalist.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((stageId == null) ? 0 : stageId.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
		PlannedSample other = (PlannedSample) obj;
		if (biosampleId == null) {
			if (other.biosampleId != null)
				return false;
		} else if (!biosampleId.equals(other.biosampleId))
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
		if (datalist == null) {
			if (other.datalist != null)
				return false;
		} else if (!datalist.equals(other.datalist))
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
		if (stageId == null) {
			if (other.stageId != null)
				return false;
		} else if (!stageId.equals(other.stageId))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}
	
}
