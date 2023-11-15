package com.idorsia.research.spirit.core.model;

import java.io.Serializable;
import java.util.Date;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.dto.IObject;

public class Study implements Serializable, Comparable<Study>, IObject {

	private static final long serialVersionUID = 9124544249743791159L;
	private Integer id = Constants.NEWTRANSIENTID;
	private String comments;
	private Date creDate;
	private String ivv;
	private String description;
	private String project;
	private String writeUsers;
	private Date updDate;
	private String updUser;
	private String creUser;
	private String readUsers;
	private String studyId;
	private String status;
	private Integer departmentId;
	private String licenceNo;
	private String rndExperimenter;
	private String blindUsers;
	private Integer department2Id;
	private Integer department3Id;
	private Integer synchroSamples;
	private String owner;
	private String studyType;
	private String elb;

	public Study() {
		super();
	}

	public Study(Integer id, String studyId, Integer blind) {
		super();
		this.id = id;
		this.studyId = studyId;
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

	public String getIvv() {
		return ivv;
	}

	public void setIvv(String ivv) {
		this.ivv = ivv;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getWriteUsers() {
		return writeUsers;
	}

	public void setWriteUsers(String writeUsers) {
		this.writeUsers = writeUsers;
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

	public String getCreUser() {
		return creUser;
	}

	public void setCreUser(String creUser) {
		this.creUser = creUser;
	}

	public String getReadUsers() {
		return readUsers;
	}

	public void setReadUsers(String readUsers) {
		this.readUsers = readUsers;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	public String getLicenceNo() {
		return licenceNo;
	}

	public void setLicenceNo(String licenceNo) {
		this.licenceNo = licenceNo;
	}

	public String getRndExperimenter() {
		return rndExperimenter;
	}

	public void setRndExperimenter(String rndExperimenter) {
		this.rndExperimenter = rndExperimenter;
	}

	public String getBlindUsers() {
		return blindUsers;
	}

	public void setBlindUsers(String blindUsers) {
		this.blindUsers = blindUsers;
	}

	public Integer getDepartment2Id() {
		return department2Id;
	}

	public void setDepartment2Id(Integer department2Id) {
		this.department2Id = department2Id;
	}

	public Integer getDepartment3Id() {
		return department3Id;
	}

	public void setDepartment3Id(Integer department3Id) {
		this.department3Id = department3Id;
	}

	public Integer getSynchroSamples() {
		return synchroSamples;
	}

	public void setSynchroSamples(Integer synchroSamples) {
		this.synchroSamples = synchroSamples;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getStudyType() {
		return studyType;
	}

	public void setStudyType(String studyType) {
		this.studyType = studyType;
	}

	public String getElb() {
		return elb;
	}

	public void setElb(String elb) {
		this.elb = elb;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((blindUsers == null) ? 0 : blindUsers.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((creDate == null) ? 0 : creDate.hashCode());
		result = prime * result + ((creUser == null) ? 0 : creUser.hashCode());
		result = prime * result + ((department2Id == null) ? 0 : department2Id.hashCode());
		result = prime * result + ((department3Id == null) ? 0 : department3Id.hashCode());
		result = prime * result + ((departmentId == null) ? 0 : departmentId.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((elb == null) ? 0 : elb.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ivv == null) ? 0 : ivv.hashCode());
		result = prime * result + ((licenceNo == null) ? 0 : licenceNo.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((readUsers == null) ? 0 : readUsers.hashCode());
		result = prime * result + ((rndExperimenter == null) ? 0 : rndExperimenter.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
		result = prime * result + ((studyType == null) ? 0 : studyType.hashCode());
		result = prime * result + ((synchroSamples == null) ? 0 : synchroSamples.hashCode());
		result = prime * result + ((writeUsers == null) ? 0 : writeUsers.hashCode());
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
		Study other = (Study) obj;
		if (blindUsers == null) {
			if (other.blindUsers != null)
				return false;
		} else if (!blindUsers.equals(other.blindUsers))
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
		if (department2Id == null) {
			if (other.department2Id != null)
				return false;
		} else if (!department2Id.equals(other.department2Id))
			return false;
		if (department3Id == null) {
			if (other.department3Id != null)
				return false;
		} else if (!department3Id.equals(other.department3Id))
			return false;
		if (departmentId == null) {
			if (other.departmentId != null)
				return false;
		} else if (!departmentId.equals(other.departmentId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
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
		if (ivv == null) {
			if (other.ivv != null)
				return false;
		} else if (!ivv.equals(other.ivv))
			return false;
		if (licenceNo == null) {
			if (other.licenceNo != null)
				return false;
		} else if (!licenceNo.equals(other.licenceNo))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		if (readUsers == null) {
			if (other.readUsers != null)
				return false;
		} else if (!readUsers.equals(other.readUsers))
			return false;
		if (rndExperimenter == null) {
			if (other.rndExperimenter != null)
				return false;
		} else if (!rndExperimenter.equals(other.rndExperimenter))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (studyId == null) {
			if (other.studyId != null)
				return false;
		} else if (!studyId.equals(other.studyId))
			return false;
		if (studyType == null) {
			if (other.studyType != null)
				return false;
		} else if (!studyType.equals(other.studyType))
			return false;
		if (synchroSamples == null) {
			if (other.synchroSamples != null)
				return false;
		} else if (!synchroSamples.equals(other.synchroSamples))
			return false;
		if (writeUsers == null) {
			if (other.writeUsers != null)
				return false;
		} else if (!writeUsers.equals(other.writeUsers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getStudyId();
	}

	@Override
	public int compareTo(Study s) {
		return -(getStudyId()==null?"":getStudyId()).compareTo(s.getStudyId()==null?"":s.getStudyId());
	}

}
