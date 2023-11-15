package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class AssayResultValue implements Serializable, IObject {

	private static final long serialVersionUID = -1093691808641119874L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String textValue;
	private Integer assayAttributeId;
	private Integer assayResultId;
	private Integer documentId;
	private Date creDate;
	private String creUser;
	private Date updDate;
	private String updUser;

	public AssayResultValue() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public Integer getAssayAttributeId() {
		return assayAttributeId;
	}

	public void setAssayAttributeId(Integer assayAttributeId) {
		this.assayAttributeId = assayAttributeId;
	}

	public Integer getAssayResultId() {
		return assayResultId;
	}

	public void setAssayResultId(Integer assayResultId) {
		this.assayResultId = assayResultId;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
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
		result = prime * result + ((assayAttributeId == null) ? 0 : assayAttributeId.hashCode());
		result = prime * result + ((assayResultId == null) ? 0 : assayResultId.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((documentId == null) ? 0 : documentId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((textValue == null) ? 0 : textValue.hashCode());
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
		AssayResultValue other = (AssayResultValue) obj;
		if (assayAttributeId == null) {
			if (other.assayAttributeId != null)
				return false;
		} else if (!assayAttributeId.equals(other.assayAttributeId))
			return false;
		if (assayResultId == null) {
			if (other.assayResultId != null)
				return false;
		} else if (!assayResultId.equals(other.assayResultId))
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
		if (documentId == null) {
			if (other.documentId != null)
				return false;
		} else if (!documentId.equals(other.documentId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (textValue == null) {
			if (other.textValue != null)
				return false;
		} else if (!textValue.equals(other.textValue))
			return false;
		return true;
	}
	
	

}