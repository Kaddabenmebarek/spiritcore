package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Assignment implements Serializable, IObject{

	private static final long serialVersionUID = 6613294756340063985L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer no;
	private Integer stageId;
	private Integer biosampleId;
	private Integer subgroupId;
	private String name;
	private String elb;
	private String datalist;
	private Long stratification;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;
	private Date removeDate;
	
	public Assignment() {
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNo() {
		return no;
	}
	public void setNo(Integer no) {
		this.no = no;
	}
	public Integer getStageId() {
		return stageId;
	}
	public void setStageId(Integer stageId) {
		this.stageId = stageId;
	}
	public Integer getBiosampleId() {
		return biosampleId;
	}
	public void setBiosampleId(Integer biosampleId) {
		this.biosampleId = biosampleId;
	}
	public Integer getSubgroupId() {
		return subgroupId;
	}
	public void setSubgroupId(Integer subgroupId) {
		this.subgroupId = subgroupId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getElb() {
		return elb;
	}
	public void setElb(String elb) {
		this.elb = elb;
	}
	public String getDatalist() {
		return datalist;
	}
	public void setDatalist(String datalist) {
		this.datalist = datalist;
	}
	public Long getStratification() {
		return stratification;
	}
	public void setStratification(Long stratification) {
		this.stratification = stratification;
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
	public Date getRemoveDate() {
		return removeDate;
	}
	public void setRemoveDate(Date removeDate) {
		this.removeDate = removeDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((biosampleId == null) ? 0 : biosampleId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((datalist == null) ? 0 : datalist.hashCode());
		result = prime * result + ((elb == null) ? 0 : elb.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((no == null) ? 0 : no.hashCode());
		result = prime * result + ((removeDate == null) ? 0 : removeDate.hashCode());
		result = prime * result + ((stageId == null) ? 0 : stageId.hashCode());
		result = prime * result + ((stratification == null) ? 0 : stratification.hashCode());
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
		Assignment other = (Assignment) obj;
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
		if (elb == null) {
			if (other.elb != null)
				return false;
		} else if (!elb.equals(other.elb))
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
		if (removeDate == null) {
			if (other.removeDate != null)
				return false;
		} else if (!removeDate.equals(other.removeDate))
			return false;
		if (stageId == null) {
			if (other.stageId != null)
				return false;
		} else if (!stageId.equals(other.stageId))
			return false;
		if (stratification == null) {
			if (other.stratification != null)
				return false;
		} else if (!stratification.equals(other.stratification))
			return false;
		if (subgroupId == null) {
			if (other.subgroupId != null)
				return false;
		} else if (!subgroupId.equals(other.subgroupId))
			return false;
		return true;
	}
	
}
