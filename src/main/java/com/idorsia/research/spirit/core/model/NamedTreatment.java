package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class NamedTreatment implements Serializable, IObject {

	private static final long serialVersionUID = -4499840479207537187L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer studyId;
	private Integer disease;
	private String name;
	private Integer color;
	private Integer ppgTreatmentInstanceId;
	private Integer spiTreatmentId;
	private String surgery;
	private String location;
	private String comments;
	private String creUser;
	private Date creDate;
	private String updUser;
	private Date updDate;
	
	public NamedTreatment() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	public Integer getDisease() {
		return disease;
	}

	public void setDisease(Integer disease) {
		this.disease = disease;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public Integer getPpgTreatmentInstanceId() {
		return ppgTreatmentInstanceId;
	}

	public void setPpgTreatmentInstanceId(Integer ppgTreatmentInstanceId) {
		this.ppgTreatmentInstanceId = ppgTreatmentInstanceId;
	}

	public Integer getSpiTreatmentId() {
		return spiTreatmentId;
	}

	public void setSpiTreatmentId(Integer spiTreatmentId) {
		this.spiTreatmentId = spiTreatmentId;
	}

	public String getSurgery() {
		return surgery;
	}

	public void setSurgery(String surgery) {
		this.surgery = surgery;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public Date getCreDate() {
		return creDate;
	}

	public void setCreDate(Date creDate) {
		this.creDate = creDate;
	}

	public String getUpdUser() {
		return updUser;
	}

	public void setUpdUser(String updUser) {
		this.updUser = updUser;
	}

	public Date getUpdDate() {
		return updDate;
	}

	public void setUpdDate(Date updDate) {
		this.updDate = updDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((disease == null) ? 0 : disease.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ppgTreatmentInstanceId == null) ? 0 : ppgTreatmentInstanceId.hashCode());
		result = prime * result + ((spiTreatmentId == null) ? 0 : spiTreatmentId.hashCode());
		result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
		result = prime * result + ((surgery == null) ? 0 : surgery.hashCode());
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
		NamedTreatment other = (NamedTreatment) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
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
		if (disease == null) {
			if (other.disease != null)
				return false;
		} else if (!disease.equals(other.disease))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ppgTreatmentInstanceId == null) {
			if (other.ppgTreatmentInstanceId != null)
				return false;
		} else if (!ppgTreatmentInstanceId.equals(other.ppgTreatmentInstanceId))
			return false;
		if (spiTreatmentId == null) {
			if (other.spiTreatmentId != null)
				return false;
		} else if (!spiTreatmentId.equals(other.spiTreatmentId))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		if (surgery == null) {
			if (other.surgery != null)
				return false;
		} else if (!surgery.equals(other.surgery))
			return false;
		return true;
	}

}
