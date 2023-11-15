package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Biosample implements Serializable, IObject {

	private static final long serialVersionUID = -5923360548020370764L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String comments;
	private Date creDate;
	private String elb;
	private String localId;
	private Integer locationPos;
	private String sampleId;
	private Date updDate;
	private String updUser;
	private Integer biotypeId;
	private Integer departmentId;
	private Integer inheritedphaseId;
	private Integer quality;
	private String creUser;
	private Integer studyId;
	private Double amount;
	private Integer topParentId;
	private Integer parentId;
	private Integer containerIndex;
	private Integer attachedsamplingId;
	private String state;
	private Integer locationId;
	private String containerId;
	private String containerType;
	private Date expiryDate;
	private String lastAction;
	private Integer endphaseId;
	private Date inheritedDate;
	private Date endDate;

	public Biosample() {
		super();
	}

	public Biosample(Integer id, String sampleId, Integer biotypeId) {
		super();
		this.id = id;
		this.sampleId = sampleId;
		this.biotypeId = biotypeId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public Integer getLocationPos() {
		return locationPos;
	}

	public void setLocationPos(Integer locationPos) {
		this.locationPos = locationPos;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
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

	public Integer getBiotypeId() {
		return biotypeId;
	}

	public void setBiotypeId(Integer biotypeId) {
		this.biotypeId = biotypeId;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public Integer getInheritedphaseId() {
		return inheritedphaseId;
	}

	public void setInheritedphaseId(Integer inheritedphaseId) {
		this.inheritedphaseId = inheritedphaseId;
	}

	public Integer getQuality() {
		return quality;
	}

	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	public Integer getTopParentId() {
		return topParentId;
	}

	public void setTopParentId(Integer topParentId) {
		this.topParentId = topParentId;
	}

	public Integer getContainerIndex() {
		return containerIndex;
	}

	public void setContainerIndex(Integer containerIndex) {
		this.containerIndex = containerIndex;
	}

	public Integer getAttachedsamplingId() {
		return attachedsamplingId;
	}

	public void setAttachedsamplingId(Integer attachedsampleId) {
		this.attachedsamplingId = attachedsampleId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	public Integer getEndphaseId() {
		return endphaseId;
	}

	public void setEndphaseId(Integer endphaseId) {
		this.endphaseId = endphaseId;
	}

	public Date getInheritedDate() {
		return inheritedDate;
	}

	public void setInheritedDate(Date inheritedDate) {
		this.inheritedDate = inheritedDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((attachedsamplingId == null) ? 0 : attachedsamplingId.hashCode());
		result = prime * result + ((biotypeId == null) ? 0 : biotypeId.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((containerId == null) ? 0 : containerId.hashCode());
		result = prime * result + ((containerIndex == null) ? 0 : containerIndex.hashCode());
		result = prime * result + ((containerType == null) ? 0 : containerType.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((elb == null) ? 0 : elb.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((endphaseId == null) ? 0 : endphaseId.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inheritedDate == null) ? 0 : inheritedDate.hashCode());
		result = prime * result + ((inheritedphaseId == null) ? 0 : inheritedphaseId.hashCode());
		result = prime * result + ((lastAction == null) ? 0 : lastAction.hashCode());
		result = prime * result + ((localId == null) ? 0 : localId.hashCode());
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result + ((locationPos == null) ? 0 : locationPos.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((quality == null) ? 0 : quality.hashCode());
		result = prime * result + ((sampleId == null) ? 0 : sampleId.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
		result = prime * result + ((topParentId == null) ? 0 : topParentId.hashCode());
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
		Biosample other = (Biosample) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (attachedsamplingId == null) {
			if (other.attachedsamplingId != null)
				return false;
		} else if (!attachedsamplingId.equals(other.attachedsamplingId))
			return false;
		if (biotypeId == null) {
			if (other.biotypeId != null)
				return false;
		} else if (!biotypeId.equals(other.biotypeId))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (containerId == null) {
			if (other.containerId != null)
				return false;
		} else if (!containerId.equals(other.containerId))
			return false;
		if (containerIndex == null) {
			if (other.containerIndex != null)
				return false;
		} else if (!containerIndex.equals(other.containerIndex))
			return false;
		if (containerType == null) {
			if (other.containerType != null)
				return false;
		} else if (!containerType.equals(other.containerType))
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
		if (departmentId == null) {
			if (other.departmentId != null)
				return false;
		} else if (!departmentId.equals(other.departmentId))
			return false;
		if (elb == null) {
			if (other.elb != null)
				return false;
		} else if (!elb.equals(other.elb))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (endphaseId == null) {
			if (other.endphaseId != null)
				return false;
		} else if (!endphaseId.equals(other.endphaseId))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inheritedDate == null) {
			if (other.inheritedDate != null)
				return false;
		} else if (!inheritedDate.equals(other.inheritedDate))
			return false;
		if (inheritedphaseId == null) {
			if (other.inheritedphaseId != null)
				return false;
		} else if (!inheritedphaseId.equals(other.inheritedphaseId))
			return false;
		if (lastAction == null) {
			if (other.lastAction != null)
				return false;
		} else if (!lastAction.equals(other.lastAction))
			return false;
		if (localId == null) {
			if (other.localId != null)
				return false;
		} else if (!localId.equals(other.localId))
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (locationPos == null) {
			if (other.locationPos != null)
				return false;
		} else if (!locationPos.equals(other.locationPos))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (quality == null) {
			if (other.quality != null)
				return false;
		} else if (!quality.equals(other.quality))
			return false;
		if (sampleId == null) {
			if (other.sampleId != null)
				return false;
		} else if (!sampleId.equals(other.sampleId))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		if (topParentId == null) {
			if (other.topParentId != null)
				return false;
		} else if (!topParentId.equals(other.topParentId))
			return false;
		return true;
	}

}
