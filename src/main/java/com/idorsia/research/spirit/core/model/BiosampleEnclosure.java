package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class BiosampleEnclosure implements Serializable, IObject {

	private static final long serialVersionUID = -3355332850395784818L;
	private Integer id = Constants.NEWTRANSIENTID;
	private Integer biosampleId;
	private Integer enclosureId;
	private Integer phaseinId;
	private Integer phaseoutId;
	private Integer studyId;
	private String updUser;
	private Date updDate;
	private String creUser;
	private Date creDate;

	public BiosampleEnclosure() {
		super();
	}

	public BiosampleEnclosure(Integer id, Integer biosampleId, Integer enclosureId, Integer phaseinId) {
		super();
		this.id = id;
		this.biosampleId = biosampleId;
		this.enclosureId = enclosureId;
		this.phaseinId = phaseinId;
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

	public Integer getEnclosureId() {
		return enclosureId;
	}

	public void setEnclosureId(Integer enclosureId) {
		this.enclosureId = enclosureId;
	}

	public Integer getPhaseinId() {
		return phaseinId;
	}

	public void setPhaseinId(Integer phaseinId) {
		this.phaseinId = phaseinId;
	}

	public Integer getPhaseoutId() {
		return phaseoutId;
	}

	public void setPhaseoutId(Integer phaseoutId) {
		this.phaseoutId = phaseoutId;
	}

	public Integer getStudyId() {
		return studyId;
	}

	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
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

	@Override
	public int hashCode() {
		return biosampleId.hashCode() + enclosureId.hashCode() + phaseinId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BiosampleEnclosure other = (BiosampleEnclosure) obj;
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
		if (enclosureId == null) {
			if (other.enclosureId != null)
				return false;
		} else if (!enclosureId.equals(other.enclosureId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (phaseinId == null) {
			if (other.phaseinId != null)
				return false;
		} else if (!phaseinId.equals(other.phaseinId))
			return false;
		if (phaseoutId == null) {
			if (other.phaseoutId != null)
				return false;
		} else if (!phaseoutId.equals(other.phaseoutId))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		return true;
	}


}
