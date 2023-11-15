package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Sampling implements Serializable, IObject {

	private static final long serialVersionUID = 876118843444407903L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer biotypeId;
	private Integer namedsamplingId;
	private Integer weighingRequired;
	private Integer parentSamplingId;
	private Integer locIndex;
	private Integer commentsRequired;
	private String containerType;
	private Integer lengthRequired;
	private String comments;
	private Double amount;
	private String sampleName;
	private Integer rowNumber;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public Sampling() {
		super();
	}

	public Sampling(Integer id, Integer biotypeId, Integer weighingRequired, Integer commentsRequired,
			Integer lengthRequired) {
		super();
		this.id = id;
		this.biotypeId = biotypeId;
		this.weighingRequired = weighingRequired;
		this.commentsRequired = commentsRequired;
		this.lengthRequired = lengthRequired;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBiotypeId() {
		return biotypeId;
	}

	public void setBiotypeId(Integer biotypeId) {
		this.biotypeId = biotypeId;
	}

	public Integer getNamedsamplingId() {
		return namedsamplingId;
	}

	public void setNamedsamplingId(Integer namedsamplingId) {
		this.namedsamplingId = namedsamplingId;
	}

	public Integer getWeighingRequired() {
		return weighingRequired;
	}

	public void setWeighingRequired(Integer weighingRequired) {
		this.weighingRequired = weighingRequired;
	}

	public Integer getParentSamplingId() {
		return parentSamplingId;
	}

	public void setParentSamplingId(Integer parentSamplingId) {
		this.parentSamplingId = parentSamplingId;
	}

	public Integer getLocIndex() {
		return locIndex;
	}

	public void setLocIndex(Integer locIndex) {
		this.locIndex = locIndex;
	}

	public Integer getCommentsRequired() {
		return commentsRequired;
	}

	public void setCommentsRequired(Integer commentsRequired) {
		this.commentsRequired = commentsRequired;
	}

	public Integer getLengthRequired() {
		return lengthRequired;
	}

	public void setLengthRequired(Integer lengthRequired) {
		this.lengthRequired = lengthRequired;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public Integer getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(Integer rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getContainerType() {
		return containerType;
	}

	public void setContainerType(String containerType) {
		this.containerType = containerType;
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
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((biotypeId == null) ? 0 : biotypeId.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((commentsRequired == null) ? 0 : commentsRequired.hashCode());
		result = prime * result + ((containerType == null) ? 0 : containerType.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lengthRequired == null) ? 0 : lengthRequired.hashCode());
		result = prime * result + ((locIndex == null) ? 0 : locIndex.hashCode());
		result = prime * result + ((namedsamplingId == null) ? 0 : namedsamplingId.hashCode());
		result = prime * result + ((parentSamplingId == null) ? 0 : parentSamplingId.hashCode());
		result = prime * result + ((rowNumber == null) ? 0 : rowNumber.hashCode());
		result = prime * result + ((sampleName == null) ? 0 : sampleName.hashCode());
		result = prime * result + ((weighingRequired == null) ? 0 : weighingRequired.hashCode());
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
		Sampling other = (Sampling) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
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
		if (commentsRequired == null) {
			if (other.commentsRequired != null)
				return false;
		} else if (!commentsRequired.equals(other.commentsRequired))
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lengthRequired == null) {
			if (other.lengthRequired != null)
				return false;
		} else if (!lengthRequired.equals(other.lengthRequired))
			return false;
		if (locIndex == null) {
			if (other.locIndex != null)
				return false;
		} else if (!locIndex.equals(other.locIndex))
			return false;
		if (namedsamplingId == null) {
			if (other.namedsamplingId != null)
				return false;
		} else if (!namedsamplingId.equals(other.namedsamplingId))
			return false;
		if (parentSamplingId == null) {
			if (other.parentSamplingId != null)
				return false;
		} else if (!parentSamplingId.equals(other.parentSamplingId))
			return false;
		if (rowNumber == null) {
			if (other.rowNumber != null)
				return false;
		} else if (!rowNumber.equals(other.rowNumber))
			return false;
		if (sampleName == null) {
			if (other.sampleName != null)
				return false;
		} else if (!sampleName.equals(other.sampleName))
			return false;
		if (weighingRequired == null) {
			if (other.weighingRequired != null)
				return false;
		} else if (!weighingRequired.equals(other.weighingRequired))
			return false;
		return true;
	}

}
